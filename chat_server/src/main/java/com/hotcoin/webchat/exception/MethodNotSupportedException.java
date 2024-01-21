package com.hotcoin.webchat.exception;

public class MethodNotSupportedException extends Exception  {
    // 提供无参数的构造方法
    public MethodNotSupportedException() {
    }

    // 提供一个有参数的构造方法，可自动生成
    public MethodNotSupportedException(String message) {
        super(message);// 把参数传递给Throwable的带String参数的构造方法
    }
}
