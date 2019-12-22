package com.mobei.member.service;

import com.mobei.entity.AppEntity;
import org.springframework.web.bind.annotation.GetMapping;

public interface MemberService {

    /**
     * 会员调用微信
     * @return
     */
    @GetMapping("/member2Wechat")
    AppEntity member2Wechat();

}
