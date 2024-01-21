package com.hotcoin.webchat;

import com.hotcoin.webchat.common.ChatServer;
import com.hotcoin.webchat.common.TextWebSocketFrameHandler;
import io.netty.channel.ChannelFuture;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.net.InetSocketAddress;

//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@EnableTransactionManagement
@SpringBootApplication
//@ImportResource(locations = "classpath*:/spring-context.xml")
public class NettyWebSocketServerWebApplication  implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(NettyWebSocketServerWebApplication.class);
    @Value("${wss.server.host}")
    private String host;

    @Value("${wss.server.port}")
    private Integer port;

    @Autowired
    private ChatServer chatServer;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(NettyWebSocketServerWebApplication.class);
        app.setWebEnvironment(false);
        app.run(args);
       // SpringApplication.run(NettyWebSocketServerWebApplication.class, args);
    }

    @Bean
    public ChatServer chatServer() {
        return new ChatServer();
    }

    @Override
    public void run(String... args) throws Exception {
        InetSocketAddress address = new InetSocketAddress(host, port);
        ChannelFuture future = chatServer.start(address);
        logger.info("service_otc_chat start on host:{} port(s):{}",host,port);
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                chatServer.destroy();
            }
        });
        future.channel().closeFuture().syncUninterruptibly();

    }
    
}
