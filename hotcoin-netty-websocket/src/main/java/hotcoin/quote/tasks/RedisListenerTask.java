package hotcoin.quote.tasks;

import hotcoin.quote.listener.RedisMqListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.tasks
 * @ClassName: RedisListenerTask
 * @Author: hf
 * @Description: 监听类启动类
 * @Date: 2019/5/8 14:01
 * @Version: 1.0
 */
@Component
public class RedisListenerTask implements ApplicationRunner {
    @Autowired
    private RedisMqListener redisMqListener;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        /**
         * 暂时不需要启用redis监听
         */
        //redisMqListener.start();
    }
}
