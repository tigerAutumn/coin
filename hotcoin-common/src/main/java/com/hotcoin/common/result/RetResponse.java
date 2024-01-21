package com.hotcoin.common.result;

import com.hotcoin.common.Enum.ErrorCode;

/**
 * @Description: 将结果转换为封装后的对象
 * @author
 * @date 2018/4/19 09:45
 */
public class RetResponse {

    private final static String SUCCESS = "success";

    public static <T> RetResult<T> makeOKRsp() {
        return new RetResult<T>().setCode(ErrorCode.SUCCESS).setMsg(SUCCESS);
    }

    public static <T> RetResult<T> makeOKRsp(T data) {
        return new RetResult<T>().setCode(ErrorCode.SUCCESS).setMsg(SUCCESS).setData(data);
    }

    public static <T> RetResult<T> makeErrRsp(String message) {
        return new RetResult<T>().setCode(ErrorCode.FAIL).setMsg(SUCCESS);
    }

    public static <T> RetResult<T> makeRsp(String code, String msg) {
        return new RetResult<T>().setCode(code).setMsg(msg);
    }

    public static <T> RetResult<T> makeRsp(String code, String msg, T data) {
        return new RetResult<T>().setCode(code).setMsg(msg).setData(data);
    }
}
