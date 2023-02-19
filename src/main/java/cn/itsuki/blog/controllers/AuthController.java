package cn.itsuki.blog.controllers;

import cn.itsuki.blog.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class AuthController {
    @Autowired
    private AuthService authService;

    @MutationMapping
    public String login(@Argument String password) {
        return this.authService.login(password);
    }
}
