package cn.itsuki.blog.security;

import cn.itsuki.blog.entities.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * token 过滤器
 *
 * @author: itsuki
 * @create: 2021-09-14 20:54
 **/
public class BearerTokenFilter extends OncePerRequestFilter {
    // 前缀
    private static final String BEARER_PREFIX = "Bearer ";
    @Autowired
    private TokenUtils tokenUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String bearerToken = request.getHeader("Authorization");
        // 获取请求头token
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            bearerToken = bearerToken.substring(BEARER_PREFIX.length());
        }

        if (!StringUtils.isEmpty(bearerToken)) {
            Admin user = tokenUtils.decodeJwtToken(bearerToken);

            // 设置 user 到上下文中
            GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().toString());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null,
                    Arrays.asList(authority));
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}
