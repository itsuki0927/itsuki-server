package cn.itsuki.blog.security;

import cn.itsuki.blog.entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * security 配置
 *
 * @author: itsuki
 * @create: 2021-09-14 21:32
 **/
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * 身份验证入口
     */
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    /**
     * 权限拒绝处理
     */
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    /**
     * 注入 token filter bean
     *
     * @return bean
     * @throws Exception
     */
    @Bean
    public BearerTokenFilter authenticationTokenFilter() throws Exception {
        return new BearerTokenFilter();
    }

    /**
     * 注入 password encode bean
     *
     * @return bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置 mvc
     */
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedHeaders("*")
                        .allowedMethods("*").allowedOrigins("*");
            }
        };
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors();
        httpSecurity.csrf().disable();
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.exceptionHandling() //
                .authenticationEntryPoint(authenticationEntryPoint) //
                .accessDeniedHandler(accessDeniedHandler);

        httpSecurity.authorizeRequests().antMatchers("/admin/login").anonymous()
                .antMatchers(HttpMethod.GET, "/admin/current-user").hasAnyRole(Role.ADMIN.toString())
                .anyRequest().authenticated();

        // token 处理
        httpSecurity.addFilterBefore(authenticationTokenFilter(),
                UsernamePasswordAuthenticationFilter.class);
    }

}
