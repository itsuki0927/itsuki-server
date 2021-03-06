package cn.itsuki.blog.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * cors 配置
 *
 * @author: itsuki
 * @create: 2021-09-15 20:46
 **/
@Configuration
public class GlobalCorsConfig {
    @Value("${cors.webUrl}")
    private String webUrl;
    @Value("${cors.adminUrl}")
    private String adminUrl;

    /**
     * 允许跨域调用的过滤器
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        //允许指定域名进行跨域调用
        config.addAllowedOrigin(webUrl);
        config.addAllowedOrigin(adminUrl);
        //允许跨越发送cookie
        config.setAllowCredentials(true);
        //放行全部原始头信息
        config.addAllowedHeader("*");
        //允许所有请求方法跨域调用
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
