package cn.itsuki.blog.logging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 切面配置
 *
 * @author: itsuki
 * @create: 2021-09-14 19:31
 **/
@Configuration
@EnableAspectJAutoProxy
public class LoggingAspectConfiguration {
    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }
}
