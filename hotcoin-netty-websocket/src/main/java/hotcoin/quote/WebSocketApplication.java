package hotcoin.quote;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ProjectName: finance-server
 * @Package: org.quote
 * @ClassName: ApplicationStart
 * @Author: hf
 * @Description:
 * @Date: 2019/4/29 12:01
 * @Version: 1.0
 */
@SpringBootApplication
@Slf4j
@RestController
public class WebSocketApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebSocketApplication.class, args);
    }

}

