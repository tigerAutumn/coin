package com.qkwl.web.exception;

public class APISecurityPermissionException extends RuntimeException{
    //状态码默认500
    private int statusCode = 500;

    public APISecurityPermissionException(int statusCode,String message) {
        super(message);
        this.statusCode = statusCode;
    }
    public APISecurityPermissionException(String s, String message) {
        super(message);
    }

    public int getStatusCode() {
        return statusCode;
    }
}