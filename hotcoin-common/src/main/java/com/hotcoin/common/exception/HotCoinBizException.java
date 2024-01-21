package com.hotcoin.common.exception;

import com.hotcoin.common.Enum.ErrorCode;

public class HotCoinBizException extends RuntimeException {


    private static final long serialVersionUID = 7718828512143293558L;

    private ErrorCode code;

    public HotCoinBizException(){

    }

    public HotCoinBizException(ErrorCode code) {
    	super(code.getDescr());
        this.code = code;
    }

    public HotCoinBizException(ErrorCode code,String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public HotCoinBizException(ErrorCode code,String message) {
        super(message);
        this.code = code;
    }

    public HotCoinBizException(ErrorCode code,Throwable cause) {
        super(cause);
        this.code = code;
    }

    public ErrorCode getCode() {
        return this.code;
    }
}
