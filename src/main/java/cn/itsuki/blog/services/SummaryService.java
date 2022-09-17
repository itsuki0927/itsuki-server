package cn.itsuki.blog.services;

import cn.itsuki.blog.entities.requests.ArticleSearchRequest;
import cn.itsuki.blog.entities.responses.SiteSummaryResponse;
import cn.itsuki.blog.repositories.CommentRepository;
import cn.itsuki.blog.repositories.TagRepository;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author: itsuki
 * @create: 2022-07-22 08:06
 **/
@Service
public class SummaryService implements GraphQLQueryResolver {
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CommentRepository commentRepository;

    public SiteSummaryResponse summary() {
        long article = articleService.count(new ArticleSearchRequest());
        long tag = tagRepository.count();
        long comment = commentRepository.totalComment();
        long guestbook = commentRepository.totalGuestbook();
        SiteSummaryResponse response = new SiteSummaryResponse();
        response.setArticle(article);
        response.setTag(tag);
        response.setComment(comment);
        response.setGuestbook(guestbook);
        LocalDateTime startTime = LocalDateTime.parse("2019-11-27T11:50:55");
        response.setStartTime(startTime);

        return response;
    }
}
