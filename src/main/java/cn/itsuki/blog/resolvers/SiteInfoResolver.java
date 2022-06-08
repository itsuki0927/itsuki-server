package cn.itsuki.blog.resolvers;

import cn.itsuki.blog.entities.Article;
import cn.itsuki.blog.entities.BlackList;
import cn.itsuki.blog.entities.Category;
import cn.itsuki.blog.entities.Tag;
import cn.itsuki.blog.entities.responses.SiteInfoResponse;
import cn.itsuki.blog.repositories.CategoryRepository;
import cn.itsuki.blog.repositories.TagRepository;
import cn.itsuki.blog.services.ArticleService;
import cn.itsuki.blog.services.BlackListService;
import graphql.kickstart.tools.GraphQLResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: itsuki
 * @create: 2022-05-10 14:25
 **/
@Slf4j
@Component
public class SiteInfoResolver implements GraphQLResolver<SiteInfoResponse> {

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private BlackListService blackListService;

    public BlackList blackList() {
        return blackListService.blacklist();
    }
}
