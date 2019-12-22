package com.mobei.member.service.impl;

import com.mobei.entity.AppEntity;
import com.mobei.member.feign.WechatServiceFeign;
import com.mobei.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberServiceImpl implements MemberService {

    @Autowired
    private WechatServiceFeign feign;

    @Override
    public AppEntity member2Wechat() {
        return feign.getApp();
    }
}
