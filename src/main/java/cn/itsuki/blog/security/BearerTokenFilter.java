package cn.itsuki.blog.security;

import cn.itsuki.blog.entities.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * token过滤器 配置
 *
 * @author: itsuki
 * @create: 2021-09-14 20:54
 **/
//@Component
public class BearerTokenFilter extends OncePerRequestFilter {
    // 前缀
    private static final String BEARER_PREFIX = "Bearer ";
    @Value("${admin.email}")
    private String adminEmail;

    private Member formatUser(UserRecord user) {
        Member member = new Member();
        member.setUid(user.getUid());
        member.setEmail(user.getEmail());
        member.setProvider(user.getProviderData()[0].getProviderId());
        member.setAvatar(user.getPhotoUrl());
        if (user.getDisplayName() != null) {
            member.setNickname(user.getDisplayName());
        }
        if (user.getEmail() != null) {
            member.setNickname(user.getEmail());
        }
        return member;
    }

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
                    var idToken = FirebaseAuth.getInstance().verifyIdToken(token);
                    var user = FirebaseAuth.getInstance().getUser(idToken.getUid());
                    Member member = formatUser(user);
                    String role = user.getEmail().equals(adminEmail) ? "ROLE_ADMIN" : "ROLE_USER";
                    // 设置 user 到上下文中
                    var authority = new SimpleGrantedAuthority(role);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(member, null,
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
