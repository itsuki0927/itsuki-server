package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.BlackList;
import cn.itsuki.blog.entities.requests.UpdateBlackListInput;
import cn.itsuki.blog.services.BlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

@Controller
public class BlackListController {
    @Autowired
    private BlackListService blackListService;

    @Secured("ROLE_ADMIN")
    @MutationMapping
    public BlackList updateBlackList(@Argument UpdateBlackListInput input) {
        return this.blackListService.updateBlackList(input);
    }

    @QueryMapping
    public BlackList blacklist() {
        return this.blackListService.blacklist();
    }
}
