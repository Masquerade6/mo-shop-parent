package com.mobei.member.feign;

import com.mobei.wechat.service.VerificaCodeService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("app-wechat")
public interface VerificaCodeServiceFeign extends VerificaCodeService {
}
