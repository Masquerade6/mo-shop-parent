package com.mobei.base;

import lombok.Data;

@Data
public class BaseResponse<T> {

    //返回码
    private Integer code;
    //消息
    private String msg;
    //
    private T data;

    public BaseResponse() {

    }

    public BaseResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
