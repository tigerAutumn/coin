package hotcoin.quote.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import hotcoin.quote.standard.ServerEndpointExporter;

/**
 * @ProjectName: finance-server
 * @Package: org.quote.config
 * @ClassName: WebSocketConfig
 * @Author: hf
 * @Description:
 * @Date: 2019/4/29 13:34
 * @Version: 1.0
 */
@Configuration
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
