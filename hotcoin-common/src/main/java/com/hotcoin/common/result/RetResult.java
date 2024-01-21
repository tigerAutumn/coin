package com.hotcoin.common.result;

import com.hotcoin.common.Enum.ErrorCode;

/**
 * @Description: 返回对象实体
 * @author
 * @date 2018/4/19 09:43
 */
public class RetResult<T> {

    public String code;

    private String msg;

    private T data;

    public RetResult<T> setCode(ErrorCode errorCode) {
        this.code = errorCode.getName();
        return this;
    }

    public String getCode() {
        return code;
    }

    public RetResult<T> setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public RetResult<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public RetResult<T> setData(T data) {
        this.data = data;
        return this;
    }

}