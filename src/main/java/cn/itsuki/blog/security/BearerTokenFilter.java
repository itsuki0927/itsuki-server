package cn.itsuki.blog.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * token过滤器 配置
 *
 * @author: itsuki
 * @create: 2021-09-14 20:54
 **/
public class BearerTokenFilter extends OncePerRequestFilter {
    // 前缀
    private static final String BEARER_PREFIX = "Bearer ";
    @Autowired
    private TokenUtils tokenUtils;

    @Value("admin.email")
    private String adminEmail;

    @Override
    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain filterChain) throws jakarta.servlet.ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token != null) {
            // 获取请求头token
            if (token.startsWith(BEARER_PREFIX)) {
                token = token.substring(BEARER_PREFIX.length());
            }

            if (token.length() > 0) {
                try {
                    var user = FirebaseAuth.getInstance().verifyIdToken(token);
                    System.out.println("email:" + user.getEmail());
                    String role = user.getEmail().equals(adminEmail) ? "ROLE_ADMIN" : "ROLE_USER";
                    System.out.println("role:" + role);
                    // 设置 user 到上下文中
                    var authority = new SimpleGrantedAuthority(role);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null,
                            List.of(authority));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (FirebaseAuthException e) {
                    System.out.println("BearerTokenFilter: " + e);
                    throw new RuntimeException(e);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
