package com.mobei.member.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mobei.base.BaseApiService;
import com.mobei.base.BaseResponse;
import com.mobei.constants.Constants;
import com.mobei.core.token.GenerateToken;
import com.mobei.member.mapper.UserMapper;
import com.mobei.member.mapper.entity.UserDo;
import com.mobei.member.service.QQAuthoriService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QQAuthoriServiceImpl extends BaseApiService<JSONObject> implements QQAuthoriService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private GenerateToken generateToken;

    @Override
    public BaseResponse<JSONObject> findByOpenId(String qqOpenId) {
        //1.根据qqOpenId查询用户信息
        if (StringUtils.isEmpty(qqOpenId)) {
            return setResultError("qqOpenId不能为空!");
        }
        UserDo userDo = userMapper.findByOpenId(qqOpenId);
        //2.如果没有查询到直接返回状态码203
        if (userDo == null) {
            return setResultError(Constants.HTTP_RES_CODE_NOTUSER_203, "根据qqOpenId没有查询到用户信息");
        }
        //3.如果能够查询到用户信息的话返回对应的用户信息token
        String keyPrefix = Constants.MEMBER_TOKEN_KEYPREFIX + Constants.LOGIN_QQ_OPENID;
        Long userId = userDo.getUserId();
        String userToken = generateToken.createToken(keyPrefix, userId + "");
        JSONObject data = new JSONObject();
        data.put("token", userToken);
        return setResultSuccess(data);
    }
}
