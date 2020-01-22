package com.mobei.member.feign;

import com.mobei.member.service.MemberService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("app-mobei-member")
public interface MemberServiceFeign extends MemberService {
}
