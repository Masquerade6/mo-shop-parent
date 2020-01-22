package com.mobei.member.feign;

import com.mobei.member.service.MemberRegisterService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("app-mobei-member")
public interface MemberRegisterServiceFeign extends MemberRegisterService {
}
