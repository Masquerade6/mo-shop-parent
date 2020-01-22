package com.mobei.member.feign;

import com.mobei.member.service.QQAuthoriService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("app-mobei-member")
public interface QQAuthoriFeign extends QQAuthoriService {
}
