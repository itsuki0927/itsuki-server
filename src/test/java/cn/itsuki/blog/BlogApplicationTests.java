package cn.itsuki.blog;

import cn.itsuki.blog.entities.responses.SiteSummaryResponse;
import cn.itsuki.blog.services.SummaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureHttpGraphQlTester
class BlogApplicationTests {


    @Autowired
    HttpGraphQlTester graphQlTester;

    @MockBean
    SummaryService summaryService;


    @Test
    void allPosts() {
        SiteSummaryResponse response = new SiteSummaryResponse();
        response.setBlog(1);
        when(this.summaryService.summary())
                .thenReturn(response);

        //the `author` and `comments` requires a `DataLoader` which currently only works in a web environment.
        var allPosts = "\"" +
                "query summary {" +
                "blog" +
                "} \"";
        graphQlTester.document(allPosts)
                .execute()
                .path("allPosts[*].title")
                .entityList(String.class)
                .satisfies(titles -> assertThat(titles).anyMatch(s -> s.startsWith("DGS POST")));

        verify(this.summaryService, times(1)).summary();
    }

    @Test
    void contextLoads() {
    }

}
