package cn.itsuki.blog.resolvers;

import cn.itsuki.blog.entities.BlackList;
import cn.itsuki.blog.entities.responses.SiteInfoResponse;
import cn.itsuki.blog.repositories.TagRepository;
import cn.itsuki.blog.services.ArticleService;
import cn.itsuki.blog.services.BlackListService;
import graphql.kickstart.tools.GraphQLResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: itsuki
 * @create: 2022-05-10 14:25
 **/
@Slf4j
@Component
public class SiteInfoResolver implements GraphQLResolver<SiteInfoResponse> {

    @Autowired
    private BlackListService blackListService;

    public BlackList blackList() {
        return blackListService.blacklist();
    }
}
