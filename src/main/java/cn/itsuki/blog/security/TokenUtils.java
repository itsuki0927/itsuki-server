package cn.itsuki.blog.security;

import cn.itsuki.blog.entities.Admin;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 * token 工具类
 *
 * @author: itsuki
 * @create: 2021-09-14 20:39
 **/
@Component
public class TokenUtils {
    private static final String CLAIM_KEY = "sub";

    // 密钥
    @Value("${jwt.secret}")
    private String secret;

    // 过期时间
    @Value("${jwt.expiration-in-seconds}")
    private long expirationInSeconds;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 获取过期时间
     *
     * @return 过期时间
     */
    public long getExpirationInSeconds() {
        return expirationInSeconds;
    }

    /**
     * 根据用户信息创建token
     *
     * @param user 用户
     * @return token
     */
    public String createJwtToken(Admin user) {
        final Date expiration = new Date(System.currentTimeMillis() + expirationInSeconds * 1000);

        try {
            return Jwts.builder().claim(CLAIM_KEY, objectMapper.writeValueAsString(user)).setExpiration(expiration)
                    .signWith(SignatureAlgorithm.HS512, secret).compact();
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Cannot create JWT token", ex);
        }
    }

    public Admin decodeJwtToken(String jwtToken) {
        Claims claims;

        try {
            claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwtToken).getBody();
        } catch (Exception ex) {
            throw new BadCredentialsException(ex.getMessage(), ex);
        }
        String payload = (String) claims.get(CLAIM_KEY);
        try {
            return objectMapper.readValue(payload, Admin.class);
        } catch (IOException ex) {
            throw new BadCredentialsException("Invalid JWT token", ex);
        }
    }
}
