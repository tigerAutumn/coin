package hotcoin.quote.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.utils
 * @ClassName: SpringContextUtil
 * @Author: hf
 * @Description:
 * @Date: 2019/5/8 14:52
 * @Version: 1.0
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext appCtx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appCtx = applicationContext;
    }

    /**
     * 获取applicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return appCtx;
    }

    /**
     * 通过name获取 Bean
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);

    }

    /**
     * 通过class获取Bean.
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);

    }
}
