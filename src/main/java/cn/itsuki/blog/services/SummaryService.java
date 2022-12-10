package cn.itsuki.blog.services;

import cn.itsuki.blog.entities.requests.SearchBlogInput;
import cn.itsuki.blog.entities.responses.SiteSummaryResponse;
import cn.itsuki.blog.repositories.BlogRepository;
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
    private BlogService blogService;
    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private CommentRepository commentRepository;

    public SiteSummaryResponse summary() {
        long blog = blogService.count(new SearchBlogInput());
        long tag = tagRepository.count();
        long comment = commentRepository.totalComment();
        long guestbook = commentRepository.totalGuestbook();
        int reading = blogRepository.blogReading();
        SiteSummaryResponse response = new SiteSummaryResponse();
        response.setBlog(blog);
        response.setTag(tag);
        response.setComment(comment);
        response.setGuestbook(guestbook);
        response.setReading(reading);
        LocalDateTime startTime = LocalDateTime.parse("2019-11-27T11:50:55");
        response.setStartTime(startTime);

        return response;
    }
}
