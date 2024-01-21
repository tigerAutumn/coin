package com.qkwl.web.exception;

public class LoginException extends RuntimeException{
    //状态码默认500
    private int statusCode = 500;

    public LoginException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
    public LoginException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
