package hotcoin.quote.resp;

import java.io.Serializable;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: org.quote.utils
 * @ClassName: Result
 * @Author: hf
 * @Description:
 * @Date: 2019/4/29 19:22
 * @Version: 1.0
 */
public class Result<T> implements Serializable {
    /**
     * 业务自定义状态码
     */
    private int code;
    /**
     * 请求状态描述，调试用
     */
    private String msg;
    /**
     * 请求数据，对象或数组均可
     */
    private T data;
    /**
     * 状态
     */
    private String status;
    /**
     * 时间戳 13位
     */
    private long ts;
    /**
     * 订阅主题
     */
    private String ch;

    public Result() {
    }

    /**
     * 成功时候的调用
     *
     * @param data data
     * @param <T>  t
     * @return Result
     */
    public static <T> Result<T> success(T data, String sub) {
        return new Result<T>(data, sub);
    }
    public static <T> Result<T> httpSuccess(T data) {
        return new Result<T>(data);
    }
    /**
     * 失败时候的调用
     *
     * @param codeMsg codeMsg
     * @param <T>     t
     * @return Result
     */
    public static <T> Result<T> error(CodeMsg codeMsg) {
        return new Result<T>(codeMsg);
    }
    public Result(T data) {
        // 默认200是成功
        this.code = 200;
        this.msg = "SUCCESS";
        this.data = data;
        this.ts = System.currentTimeMillis();
        this.status = "ok";
    }
    /**
     * 成功的构造函数
     *
     * @param data data
     */
    public Result(T data, String sub) {
        // 默认200是成功
        this.ch = sub;
        this.code = 200;
        this.msg = "SUCCESS";
        this.data = data;
        this.ts = System.currentTimeMillis();
        this.status = "ok";
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 失败的构造函数
     *
     * @param codeMsg codeMsg
     */
    private Result(CodeMsg codeMsg) {
        if (codeMsg != null) {
            this.code = codeMsg.getCode();
            this.msg = codeMsg.getMsg();
            this.ts = System.currentTimeMillis();
            this.status = "error";
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getCh() {
        return ch;
    }

    public void setCh(String ch) {
        this.ch = ch;
    }
    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", status='" + status + '\'' +
                ", ts=" + ts +
                '}';
    }
}
