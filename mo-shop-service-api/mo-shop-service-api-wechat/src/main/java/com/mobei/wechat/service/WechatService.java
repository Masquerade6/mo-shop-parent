package com.mobei.wechat.service;

import com.mobei.entity.AppEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 微信服务接口
 */
@Api(tags = "微信服务")
public interface WechatService {

    /**
     * 获取应用接口
     * @return
     */
    @ApiOperation("微信应用服务接口")
    @GetMapping("/getApp")
    AppEntity getApp();
}
