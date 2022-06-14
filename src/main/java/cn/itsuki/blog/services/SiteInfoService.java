package cn.itsuki.blog.services;

import cn.itsuki.blog.entities.BlackList;
import cn.itsuki.blog.entities.requests.ArticleSearchRequest;
import cn.itsuki.blog.entities.responses.SiteSummaryResponse;
import cn.itsuki.blog.repositories.CommentRepository;
import cn.itsuki.blog.repositories.TagRepository;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 网站信息 服务
 *
 * @author: itsuki
 * @create: 2021-10-24 18:16
 **/
@Service
public class SiteInfoService implements GraphQLQueryResolver {
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CommentRepository commentRepository;

    public SiteSummaryResponse getSummary() {
        long article = articleService.count(new ArticleSearchRequest());
        long tag = tagRepository.count();
        long comment = commentRepository.count();
        SiteSummaryResponse response = new SiteSummaryResponse();
        response.setArticle(article);
        response.setTag(tag);
        response.setComment(comment);

        return response;
    }
}
