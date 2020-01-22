package com.mobei.member.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mobei.base.BaseApiService;
import com.mobei.base.BaseResponse;
import com.mobei.constants.Constants;
import com.mobei.core.token.GenerateToken;
import com.mobei.core.transaction.RedisDataSoureceTransaction;
import com.mobei.core.utils.MD5Util;
import com.mobei.member.input.dto.UserLoginInputDTO;
import com.mobei.member.mapper.UserMapper;
import com.mobei.member.mapper.UserTokenMapper;
import com.mobei.member.mapper.entity.UserDo;
import com.mobei.member.mapper.entity.UserTokenDo;
import com.mobei.member.service.MemberLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberLoginServiceImpl extends BaseApiService<JSONObject> implements MemberLoginService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserTokenMapper userTokenMapper;

    @Autowired
    private GenerateToken generateToken;

    @Autowired
    private RedisDataSoureceTransaction transaction;

    /**
     * 实现类的这里不加@RequestBody使用swagger测试的时候不能传json
     *
     * 需要考虑的问题:redis的值如何与数据库的值保持一致?@Transactional不能控制Redis事务
     * 解决思路:自定义方法,使用编程式事务控制数据库事务和Redis事务:begin() commit()
     *
     * 如果Redis值与数据库值不一致:把Redis清除再重新保存
     *
     * @param userLoginInpDTO
     * @return
     */
    @Override
//    @Transactional
    public BaseResponse<JSONObject> login(@RequestBody UserLoginInputDTO userLoginInpDTO) {
        //1.验证参数
        String mobile = userLoginInpDTO.getMobile();
        if (StringUtils.isEmpty(mobile)) {
            //类似的这种模板消息一般存放在zookeeper或者分布式配置中心中
            return setResultError("手机号码不能为空!");
        }
        String password = userLoginInpDTO.getPassword();
        if (StringUtils.isEmpty(password)) {
            return setResultError("密码不能为空!");
        }

        String loginType = userLoginInpDTO.getLoginType();
        if (StringUtils.isEmpty(loginType)) {
            return setResultError("登陆类型不能为空!");
        }

        //一般不会写死,会放到配置中心
        if (!(loginType.equals(Constants.MEMBER_LOGIN_TYPE_ANDROID)
                || loginType.equals(Constants.MEMBER_LOGIN_TYPE_IOS)
                || loginType.equals(Constants.MEMBER_LOGIN_TYPE_PC))) {
            return setResultError("登陆类型出现错误!");
        }

        //设备信息
        String deviceInfor = userLoginInpDTO.getDeviceInfor();
        if (StringUtils.isEmpty(deviceInfor)) {
            return setResultError("设备信息不能为空!");
        }

        //2.对登陆密码实现加密
        String newPassword = MD5Util.MD5(password);

        //3.使用手机号码+密码查询数据库,判断用户是否存在
        UserDo userDo = userMapper.login(mobile, newPassword);
        if (userDo == null) {
            return setResultError("用户名或密码错误!");
        }

        //用户登录: token、session的区别
        //用户每一个端登录成功后会对应生成一个token令牌(临时且唯一),存放在Redis中作为key,value位userid

        TransactionStatus transactionStatus = null;

        try {
            //4.获取userid
            Long userId = userDo.getUserId();
            //4.1根据userid+logintype查询当前登录类型账号之前是否登陆过
            //   如果登陆过,清除之前redis中的token
            UserTokenDo userTokenDo = userTokenMapper.selectByUserIdAndLoginType(userId, loginType);

            //开启事务
            transactionStatus = transaction.begin();

            if (userTokenDo != null) {//之前登陆过
                String token = userTokenDo.getToken();
                Boolean isTokenRemoved = generateToken.removeToken(token);
                //如果开启Redis事务的话,删除方法会返回false
//                if (isTokenRemoved) {
                    //把token状态改为1
                    int updateTokenAvailability = userTokenMapper.updateTokenAvailability(token);
                    if (!toDaoResult(updateTokenAvailability)) {
                        return setResultError("系统错误!");
                    }
//                }
            }

            //如果有传递openid参数,修改到数据库中
            // openid关联用户账号信息
            String qqOpenId = userLoginInpDTO.getQqOpenId();
            if (!StringUtils.isEmpty(qqOpenId)) {
                userMapper.updateUserOpenId(qqOpenId, userId);
            }

            UserTokenDo userToken = new UserTokenDo();
            userToken.setUserId(userId);
            userToken.setLoginType(userLoginInpDTO.getLoginType());

            //5.生成对应的用户令牌放在Redis中
            String keyPrefix = Constants.MEMBER_TOKEN_KEYPREFIX + loginType;
            String newToken = generateToken.createToken(keyPrefix, userId + "");

            userToken.setToken(newToken);
            userToken.setDeviceInfor(deviceInfor);
            int insertUserToken = userTokenMapper.insertUserToken(userToken);

            if (toDaoResult(insertUserToken)) {
                transaction.rollback(transactionStatus);
                return setResultError("系统错误!");
            }

            JSONObject data = new JSONObject();
            data.put("token", newToken);
            transaction.commit(transactionStatus);
            return setResultSuccess(data);
        } catch (Exception e) {
            try {
                transaction.rollback(transactionStatus);
                return setResultError("系统错误!");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }
}
