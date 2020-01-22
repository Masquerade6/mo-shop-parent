package com.mobei.member.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mobei.base.BaseApiService;
import com.mobei.base.BaseResponse;
import com.mobei.constants.Constants;
import com.mobei.core.bean.MyBeanUtils;
import com.mobei.core.utils.MD5Util;
import com.mobei.member.feign.VerificaCodeServiceFeign;
import com.mobei.member.input.dto.UserInputDTO;
import com.mobei.member.mapper.UserMapper;
import com.mobei.member.mapper.entity.UserDo;
import com.mobei.member.service.MemberRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberRegisterServiceImpl extends BaseApiService<JSONObject> implements MemberRegisterService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private VerificaCodeServiceFeign verificaCodeServiceFeign;

    /**
     * 注意:这里的@RequestBody注解接口和实现方法中都需要写,接口中主要是为了feign复用,这里不写的话客户端会以key-value的形式传参
     * @param userInputDTO
     * @param registCode
     * @return
     */
    @Override
    public BaseResponse<JSONObject> register(@RequestBody UserInputDTO userInputDTO, String registCode) {
        // 1.验证参数
        String userName = userInputDTO.getUserName();
        if (StringUtils.isEmpty(userName)) {
            return setResultError("用户名称不能为空!");
        }
        String mobile = userInputDTO.getMobile();
        if (StringUtils.isEmpty(mobile)) {
            return setResultError("手机号码不能为空!");
        }
        String password = userInputDTO.getPassword();
        if (StringUtils.isEmpty(password)) {
            return setResultError("密码不能为空!");
        }

        // 2.调用微信接口,验证注册码是否正确
        BaseResponse<JSONObject> resultVerificaWeixinCode = verificaCodeServiceFeign.verificaWeixinCode(mobile, registCode);
        if (!resultVerificaWeixinCode.getCode().equals(Constants.HTTP_RES_CODE_200)) {
            return setResultError(resultVerificaWeixinCode.getMsg());
        }

        //3.对用户的密码进行加密
        String newPassWord = MD5Util.MD5(password);
        userInputDTO.setPassword(newPassWord);

        //4.调用数据库插入数据,将请求的DTO参数转换成DO
//        UserDo userDo = new UserDo();
//        BeanUtils.copyProperties(userInputDTO, userDo);
        UserDo userDo = MyBeanUtils.dtoToDo(userInputDTO, UserDo.class);
        int registerResult = userMapper.register(userDo);
        return registerResult > 0 ? setResultSuccess("注册成功") : setResultSuccess("注册失败");

    }

}

