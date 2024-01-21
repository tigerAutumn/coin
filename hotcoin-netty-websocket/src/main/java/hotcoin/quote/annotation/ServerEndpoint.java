package hotcoin.quote.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hf
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServerEndpoint {

    @AliasFor("path")
    String value() default "/";

    @AliasFor("value")
    String path() default "/";

    String host() default "0.0.0.0";

    int port() default 8080;

    /**
     * 0表示默认,可以使用默认的cpu*2方式,不建议修改该值
     * 如果调优的话,可参考该类:netty包下的 MultithreadEventLoopGroup类,默认使用: NettyRuntime.availableProcessors() * 2
     *
     * @return
     */
    int bossLoopGroupThreads() default 0;

    /**
     * 0表示默认,可以使用默认的cpu*2方式,不建议修改该值
     * 如果调优的话,可参考该类:netty包下的 MultithreadEventLoopGroup类,默认使用: NettyRuntime.availableProcessors() * 2
     *
     * @return
     */
    int workerLoopGroupThreads() default 0;

    boolean useCompressionHandler() default false;

    /**
     * if this property is not empty, means configure with application.properties
     */
    String prefix() default "";

    //------------------------- option -------------------------

    /**
     * 连接超时时间
     *
     * @return
     */
    int optionConnectTimeoutMillis() default 30000;

    int optionSoBacklog() default 128;

    //------------------------- childOption -------------------------

    int childOptionWriteSpinCount() default 16;

    int childOptionWriteBufferHighWaterMark() default 64 * 1024;

    int childOptionWriteBufferLowWaterMark() default 32 * 1024;

    int childOptionSoRcvbuf() default -1;

    int childOptionSoSndbuf() default -1;

    boolean childOptionTcpNodelay() default true;

    boolean childOptionSoKeepalive() default false;

    int childOptionSoLinger() default -1;

    boolean childOptionAllowHalfClosure() default false;

    //------------------------- idleEvent -------------------------

    /**
     * ping包检测时间
     *
     * @return
     */
    int readerIdleTimeSeconds() default 30;

    int writerIdleTimeSeconds() default 0;

    int allIdleTimeSeconds() default 0;

    //------------------------- handshake -------------------------

    int maxFramePayloadLength() default 65536;

}
