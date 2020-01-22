package com.mobei.wechat.feign;

import com.mobei.member.service.MemberService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 会员服务feign
 */
@FeignClient("app-member")
public interface MemberServiceFeign extends MemberService {
}
