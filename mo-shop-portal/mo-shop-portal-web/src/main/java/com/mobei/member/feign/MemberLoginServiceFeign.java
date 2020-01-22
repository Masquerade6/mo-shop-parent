package com.mobei.member.feign;

import com.mobei.member.service.MemberLoginService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("app-mobei-member")
public interface MemberLoginServiceFeign extends MemberLoginService {
}
