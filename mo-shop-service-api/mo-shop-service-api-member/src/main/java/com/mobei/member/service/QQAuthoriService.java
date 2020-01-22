package com.mobei.member.service;

import com.alibaba.fastjson.JSONObject;
import com.mobei.base.BaseResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户授权接口
 */
public interface QQAuthoriService {

    /**
     * 根据OpenId查询是否已经绑定,如果已经绑定则实现自动登录
     * @param qqOpenId
     * @return
     */
    @RequestMapping("/findByOpenId")
    BaseResponse<JSONObject> findByOpenId(@RequestParam("qqOpenId") String qqOpenId);

}
