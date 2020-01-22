package com.mobei.member.service.impl;

import com.mobei.base.BaseApiService;
import com.mobei.base.BaseResponse;
import com.mobei.constants.Constants;
import com.mobei.core.bean.MyBeanUtils;
import com.mobei.core.token.GenerateToken;
import com.mobei.core.type.TypeCastHelper;
import com.mobei.member.mapper.UserMapper;
import com.mobei.member.mapper.entity.UserDo;
import com.mobei.member.output.dto.UserOutDTO;
import com.mobei.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberServiceImpl extends BaseApiService<UserOutDTO> implements MemberService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private GenerateToken generateToken;

    @Override
    public BaseResponse<UserOutDTO> existMobile(String mobile) {
        // 1.验证参数
        if (StringUtils.isEmpty(mobile)) {
            return setResultError("手机号码不能为空!");
        }
        UserDo userDo = userMapper.existMobile(mobile);
        //2.根据用户手机号码查询用户信息,单独定义code表示是用户信息不存在
        if (userDo == null) {
            return setResultError(Constants.HTTP_RES_CODE_EXISTMOBILE_202, "用户不存在");
        }
        // 注意需要将敏感数据进行脱敏
//        userDo.setPassword(null);
        return setResultSuccess(MyBeanUtils.dtoToDo(userDo, UserOutDTO.class));
    }

    @Override
    public BaseResponse<UserOutDTO> getInfo(String token) {
        //1.验证token参数
        if (StringUtils.isEmpty(token)) {
            return setResultError("token不能为空");
        }

        //2.使用token查询redis中的userid
        String redisUserId = generateToken.getToken(token);
        if (StringUtils.isEmpty(redisUserId)) {
            return setResultError("token已经失效或者token错误");
        }

        //3.使用userid查询数据库用户信息
        Long userId = TypeCastHelper.toLong(redisUserId);
        UserDo userDo = userMapper.findByUserId(userId);
        if (userDo == null) {
            return setResultError("用户不存在!");
        }
        return setResultSuccess(MyBeanUtils.doToDto(userDo, UserOutDTO.class));
    }

    //token存放在pc端:coockie
    //token存放在安卓或者IOS端存放在本地文件中
    //当前存在哪些问题:用户如果退出或者修改密码时,需要对token状态进行标识
    //token如何防止伪造:没法百分百防止,只能尽量实现安全体系,在某些业务模块必须加上本人操作





}
