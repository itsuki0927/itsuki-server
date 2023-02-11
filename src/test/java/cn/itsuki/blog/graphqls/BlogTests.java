package cn.itsuki.blog.graphqls;

import cn.itsuki.blog.controllers.SiteSummaryController;
import cn.itsuki.blog.repositories.BlogTagRepository;
import cn.itsuki.blog.repositories.TagRepository;
import cn.itsuki.blog.services.SummaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;


//@Import({BlogService.class, TagRepository.class, BlogTagRepository.class})
@GraphQlTest(SiteSummaryController.class)
public class BlogTests {
    @Autowired
    private GraphQlTester tester;

    @MockBean
    private SummaryService blogService;

    @MockBean
    private TagRepository tagRepository;

    @MockBean
    private BlogTagRepository blogTagRepository;

    @Test
    public void blogs() {
        this.tester.documentName("summary")
//                .variable("recent", true)
                .execute()
//                .path("data.blogs.total")
                .path("summary.blog")
                .entity(Integer.class)
                .isEqualTo(16);
    }
}
