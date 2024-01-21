package hotcoin.quote.resp;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: org.quote.model
 * @ClassName: CodeMsg
 * @Author: hf
 * @Description:
 * @Date: 2019/4/29 19:22
 * @Version: 1.0
 */
public class CodeMsg {
    private int code;
    private String msg;
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
    /**服务端异常*/
    public static CodeMsg SUCCESS = new CodeMsg(200,"SUCCESS");
    public static CodeMsg SERVER_ERROR = new CodeMsg(100,"系统异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(101,"绑定异常参数校验异常");
    public static CodeMsg SESSION_ERROR = new CodeMsg(102,"没有SESSION！");
    public static CodeMsg REQUEST_ERROR = new CodeMsg(103,"非法请求！");
    public static CodeMsg REQUEST_OVER_LIMIT = new CodeMsg(104,"请求次数过多！");
    public static CodeMsg REQUEST_PARAM_ERROR = new CodeMsg(105,"请求参数异常！");
    private CodeMsg( ) {
    }
    private CodeMsg( int code,String msg ) {
        this.code = code;
        this.msg = msg;
    }
    /** 不定参的构造函数*/
    public CodeMsg fillArgs(Object... args) {
        int code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }
    @Override
    public String toString() {
        return "CodeMsg [code=" + code + ", msg=" + msg + "]";
    }
}
