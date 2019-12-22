package com.mobei.wechat.service;

import com.mobei.entity.AppEntity;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 微信服务接口
 */
public interface WechatService {

    /**
     * 获取应用接口
     * @return
     */
    @GetMapping("/getApp")
    AppEntity getApp();
}
