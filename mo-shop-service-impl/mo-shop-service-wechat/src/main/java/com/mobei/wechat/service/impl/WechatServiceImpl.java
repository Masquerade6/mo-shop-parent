package com.mobei.wechat.service.impl;

import com.mobei.entity.AppEntity;
import com.mobei.wechat.service.WechatService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WechatServiceImpl implements WechatService {

    @Override
    public AppEntity getApp() {
        return new AppEntity("123456", "mobei");
    }

}
