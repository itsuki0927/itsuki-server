package cn.itsuki.blog.services;

import cn.itsuki.blog.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class AuthService {
    @Autowired
    private TokenUtils tokenUtils;
    @Value("${admin.password}")
    private String adminPassword;

    public String login(String password) {
        if (password == null) {
           throw new IllegalArgumentException("请输入密码");
        }
        if (!adminPassword.equals(password)) {
            throw new IllegalArgumentException("密码不正确");
        }
        return tokenUtils.createJwtToken();
    }
}
