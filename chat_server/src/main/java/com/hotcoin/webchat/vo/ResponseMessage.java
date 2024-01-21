package com.hotcoin.webchat.vo;

import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.Date;

public class ResponseMessage<T> {

    private int code;
    private T data;
    private String msg;
    private Date time;

    public ResponseMessage() {

    }

    public ResponseMessage(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.time = new Date();
    }

    public ResponseMessage<T> error(T data,String message) {
        return new ResponseMessage<T>(44, data,message);
    }

    public ResponseMessage<T> success(T data) {
        return new ResponseMessage<T>(200, data,"成功");
    }

    public ResponseMessage<T> warn(T data) {
        return new ResponseMessage<T>(206, data,"警告");
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
