package com.qkwl.web.exception;

public class APISignSecurityPermissionException extends RuntimeException{
    //状态码默认500
    private int statusCode = 500;

    public APISignSecurityPermissionException(int statusCode,String message) {
        super(message);
        this.statusCode = statusCode;
    }
    public APISignSecurityPermissionException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
