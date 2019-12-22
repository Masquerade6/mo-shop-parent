package com.mobei.member.feign;

import com.mobei.wechat.service.WechatService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("app-wechat")
public interface WechatServiceFeign extends WechatService {

}
