package com.qkwl.common.exceptions;

import java.io.Serializable;

/**
 * 用户自定义异常
 * @author ZKF
 */
public class BCException extends Exception  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Integer code;

	public BCException() {
		super( );
	}

	public BCException(Integer code,String message) {
		super(message);
		this.code = code;
	}
	public BCException(String message) {
		super(message);

	}
	public BCException(Throwable innerException){
		super(innerException);
	}
	public BCException(String message,Throwable innerException){
		super(message,innerException);
	}

	public BCException(String message, Object... args) {
		super(String.format(message, args));
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
	
	
}
