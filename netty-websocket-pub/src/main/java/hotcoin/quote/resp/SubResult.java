package hotcoin.quote.resp;

import java.io.Serializable;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.utils
 * @ClassName: SubResult
 * @Author: hf
 * @Description:
 * @Date: 2019/5/5 11:50
 * @Version: 1.0
 */
public class SubResult<T> implements Serializable {

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
    private String subbed;

    public SubResult() {
        this.ts = System.currentTimeMillis();
        this.status = "ok";
    }
    /**
     * 成功订阅时候的调用
     *
     * @param  sub
     * @param <T>  t
     * @return Result
     */
    public static <T> SubResult<T> subSuccess(String sub) {
        return new SubResult<T>(sub);
    }
    /**
     * 成功连接/取消时候的调用
     *
     * @param <T>  t
     * @return Result
     */
    public static <T> SubResult<T> optSuccess() {
        return new SubResult<T>();
    }
    /**
     * 成功的构造函数
     *
     * @param sub
     */
    public SubResult(String sub) {
        // 默认200是成功
        this.subbed = sub;
        this.ts = System.currentTimeMillis();
        this.status = "ok";
    }

    /**
     * 失败的构造函数
     *
     * @param
     */
    public static <T> SubResult<T> error(String sub) {
        SubResult subResult = new SubResult<T>();
        subResult.ts= System.currentTimeMillis();
        subResult.setStatus("error");
        subResult.setSubbed(sub);
        return subResult;
    }
    /**
     * 连接失败/取消订阅失败的构造函数
     *
     * @param
     */
    public static <T> SubResult<T> optError() {
        SubResult subResult = new SubResult<T>();
        subResult.ts= System.currentTimeMillis();
        subResult.setStatus("error");
        return subResult;
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

    public String getSubbed() {
        return subbed;
    }

    public void setSubbed(String subbed) {
        this.subbed = subbed;
    }
}
