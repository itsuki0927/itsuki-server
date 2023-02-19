package cn.itsuki.blog.services;

import cn.itsuki.blog.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AuthService {
    @Autowired
    private TokenUtils tokenUtils;

    public String login(String password) {
        return tokenUtils.createJwtToken(password);
    }
}
