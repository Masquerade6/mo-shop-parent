package com.mobei.wechat.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mobei.base.BaseApiService;
import com.mobei.base.BaseResponse;
import com.mobei.constants.Constants;
import com.mobei.core.utils.RedisUtil;
import com.mobei.wechat.service.VerificaCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class VerificaCodeServiceImpl extends BaseApiService<JSONObject> implements VerificaCodeService {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public BaseResponse<JSONObject> verificaWeixinCode(String phone, String weixinCode) {
        if (StringUtils.isEmpty(phone)) {
            return setResultError("手机号码不能为空!");
        }

        if (StringUtils.isEmpty(weixinCode)) {
            return setResultError("注册码不能为空!");
        }

        String wexinCodeKey = Constants.WEIXINCODE_KEY + phone;
        if (!redisUtil.hasKey(wexinCodeKey)) {
            return setResultError("注册码已经过期,请重新发送验证码");
        }

//        String code = redisUtil.get(wexinCodeKey);
//        if (StringUtils.isEmpty(code)) {
//            return setResultError("注册码已经过期,请重新发送验证码");
//        }

        String code = redisUtil.get(wexinCodeKey);
        if (!code.equals(weixinCode)) {
            return setResultError("注册码不正确");
        }

        //移除验证码
        redisUtil.delete(wexinCodeKey);
        return setResultSuccess("注册码验证正确");
    }

}

