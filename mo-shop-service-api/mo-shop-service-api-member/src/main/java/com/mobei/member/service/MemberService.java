package com.mobei.member.service;

import com.mobei.entity.AppEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

//@Api:作用在类上,用来标注该类具体实现内容
//tags不加会以实现类的类名显示(如:member-service-impl),加上就会显示为自定义的标签名
@Api(tags = "会员服务")
public interface MemberService {

    /**
     * 会员调用微信
     * @return
     */
    @ApiOperation("会员调用微信服务")
    @GetMapping("/member2Wechat")
    AppEntity memberInvokeWechat();

}
