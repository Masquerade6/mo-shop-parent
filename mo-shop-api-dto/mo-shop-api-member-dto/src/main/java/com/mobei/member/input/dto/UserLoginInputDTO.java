package com.mobei.member.input.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 为什么一个接口单独定义一个DTO请求参数类:
 *  保证swagger接口文档对称
 *
 */
@Data
@ApiModel(value = "用户登录请求参数")
public class UserLoginInputDTO {

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    private String mobile;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * 登陆类型:PC、Android、IOS
     */
    @ApiModelProperty(value = "登陆类型")
    private String loginType;

    /**
     * 设备信息
     */
    @ApiModelProperty(value = "设备信息")
    private String deviceInfor;

    /**
     * qq开放id
     */
    String qqOpenId;

}
