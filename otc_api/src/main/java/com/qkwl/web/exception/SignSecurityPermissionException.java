package com.qkwl.web.exception;

public class SignSecurityPermissionException extends RuntimeException{
    //状态码默认500
    private int statusCode = 603;

    public SignSecurityPermissionException(int statusCode,String message) {
        super(message);
        this.statusCode = statusCode;
    }
    public SignSecurityPermissionException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
