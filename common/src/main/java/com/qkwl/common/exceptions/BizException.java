package com.qkwl.common.exceptions;

import com.qkwl.common.dto.Enum.ErrorCodeEnum;

/**
 * 
 * @author huangjinfeng
 */
public class BizException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final Object[] args;

  private final ErrorCodeEnum errorCodeEnum;

  public BizException(ErrorCodeEnum errorCodeEnum, Object... args) {
    super();
    this.errorCodeEnum=errorCodeEnum;
    this.args=args;	
  }

  public Object[] getArgs() {
    return args;
  }

  public ErrorCodeEnum getErrorCodeEnum() {
    return errorCodeEnum;
  }

}
