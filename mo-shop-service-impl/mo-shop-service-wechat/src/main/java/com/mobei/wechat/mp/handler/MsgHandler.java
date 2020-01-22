package com.mobei.wechat.mp.handler;

import com.mobei.base.BaseResponse;
import com.mobei.constants.Constants;
import com.mobei.core.utils.RedisUtil;
import com.mobei.core.utils.RegexUtils;
import com.mobei.member.output.dto.UserOutDTO;
import com.mobei.wechat.feign.MemberServiceFeign;
import com.mobei.wechat.mp.builder.TextBuilder;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Component
public class MsgHandler extends AbstractHandler {

    //用户发送手机验证码提示
    @Value("${mobei.weixin.registration.code.message}")
    private String registCodeMsg;
    @Value("${mobei.weixin.default.registration.code.message}")
    private String defaultRegistCodeMsg;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MemberServiceFeign memberServiceFeign;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context,
                                    WxMpService weixinService,
                                    WxSessionManager sessionManager) {

        if (!wxMessage.getMsgType().equals(WxConsts.XmlMsgType.EVENT)) {
            //TODO 可以选择将消息保存到本地
        }

        //当用户输入关键词如“你好”，“客服”等，并且有客服在线时，把消息转发给在线客服
        try {
            if (StringUtils.startsWithAny(wxMessage.getContent(), "你好", "客服")
                && weixinService.getKefuService().kfOnlineList()
                .getKfOnlineList().size() > 0) {
                return WxMpXmlOutMessage.TRANSFER_CUSTOMER_SERVICE()
                    .fromUser(wxMessage.getToUser())
                    .toUser(wxMessage.getFromUser()).build();
            }
        } catch (WxErrorException e) {
            e.printStackTrace();
        }

        //1.获取微信客户端发送的消息
        String fromContent = wxMessage.getContent();
        //2.使用正则表达式验证消息是否为手机号码格式
        if (RegexUtils.checkMobile(fromContent)) {
            //3.根据手机号码调用会员服务接口查询用户是否存在
            BaseResponse<UserOutDTO> userEntityBaseResponse = memberServiceFeign.existMobile(fromContent);
            if (Constants.HTTP_RES_CODE_200.equals(userEntityBaseResponse.getCode())) {
                return new TextBuilder().build(fromContent + "号码已经存在!", wxMessage, weixinService);
            }
            if (!Constants.HTTP_RES_CODE_EXISTMOBILE_202.equals(userEntityBaseResponse.getCode())) {
                return new TextBuilder().build(userEntityBaseResponse.getMsg(), wxMessage, weixinService);
            }
            //4.如果是手机号码格式的话随机生成4位数字注册码
            int registCode = registCode();
            String content = String.format(registCodeMsg, registCode);//将registCodeMsg中的s%替换成注册码
            //将注册码存入Redis中
            redisUtil.set(Constants.WEIXINCODE_KEY + fromContent, registCode + "");
            redisUtil.expire(Constants.WEIXINCODE_KEY + fromContent, Constants.WEIXINCODE_TIMEOUT, TimeUnit.SECONDS);
            return new TextBuilder().build(content, wxMessage, weixinService);
        }
        //否则返回默认消息,或者调用第三方机器人接口回复消息
        return new TextBuilder().build(defaultRegistCodeMsg, wxMessage, weixinService);
    }

    private int registCode() {
        int registCode = (int) (Math.random() * 9000 + 1000);
        return registCode;
    }
}
