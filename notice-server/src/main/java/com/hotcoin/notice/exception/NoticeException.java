package com.hotcoin.notice.exception;

import com.hotcoin.notice.Enum.ErrorCodeEnum;

/**
 * 
 * @author huangjinfeng
 */
public class NoticeException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final Object[] args;

  private final ErrorCodeEnum errorCodeEnum;

  public NoticeException(ErrorCodeEnum errorCodeEnum, Object... args) {
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
