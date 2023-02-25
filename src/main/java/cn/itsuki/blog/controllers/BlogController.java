package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.Blog;
import cn.itsuki.blog.entities.BlogTag;
import cn.itsuki.blog.entities.Tag;
import cn.itsuki.blog.entities.requests.CreateBlogInput;
import cn.itsuki.blog.entities.requests.SearchBlogInput;
import cn.itsuki.blog.entities.responses.BlogDetailResponse;
import cn.itsuki.blog.entities.responses.BlogSummaryResponse;
import cn.itsuki.blog.entities.responses.SearchResponse;
import cn.itsuki.blog.repositories.BlogTagRepository;
import cn.itsuki.blog.repositories.TagRepository;
import cn.itsuki.blog.services.BlogService;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class BlogController {
    @Autowired
    public BlogService blogService;

    @Autowired
    public TagRepository tagRepository;

    @Autowired
    public BlogTagRepository blogTagRepository;

    @QueryMapping
    public SearchResponse<Blog> blogs(@Argument SearchBlogInput search) {
        return this.blogService.blogs(search);
    }

    @QueryMapping
    public BlogDetailResponse blog(@Argument String path, DataFetchingEnvironment env) {
        return this.blogService.blog(path, env);
    }

    @QueryMapping
    public BlogSummaryResponse blogSummary() {
        return this.blogService.blogSummary();
    }

    @Secured("ROLE_ADMIN")
    @MutationMapping
    public Blog createBlog(@Argument CreateBlogInput input) {
        return this.blogService.createBlog(input);
    }

    @Secured("ROLE_ADMIN")
    @MutationMapping
    public int deleteBlog(@Argument Long id) {
        return this.blogService.deleteBlog(id);
    }

    @Secured("ROLE_ADMIN")
    @MutationMapping
    public Blog updateBlog(@Argument Long id, @Argument CreateBlogInput input) {
        return this.blogService.updateBlog(id, input);
    }

    @Secured("ROLE_ADMIN")
    @MutationMapping
    public int updateBlogState(@Argument List<Long> ids, @Argument Integer state) {
        return this.blogService.updateBlogState(ids, state);
    }

    @MutationMapping
    public int likeBlog(@Argument Long id, @Argument Integer counter) {
        return this.blogService.likeBlog(id, counter);
    }

    @MutationMapping
    public int readBlog(@Argument Long id) {
        return this.blogService.readBlog(id);
    }

    @MutationMapping
    public int syncBlogCommentCount(@Argument List<Long> ids) {
        return this.blogService.syncBlogCommentCount(ids);
    }

    @SchemaMapping
    public List<Tag> tags(Blog blog) {
        return tags(blog.getId());
    }

    @SchemaMapping
    public List<Tag> tags(BlogDetailResponse blog) {
        return tags(blog.getId());
    }

    private List<Tag> tags(Long blogId) {
        BlogTag probe = new BlogTag();
        probe.setBlogId(blogId);
        Example<BlogTag> example = Example.of(probe);
        List<BlogTag> blogTags = blogTagRepository.findAll(example);
        return tagRepository.findAllById(blogTags.stream().map(BlogTag::getTagId).collect(Collectors.toList()));
    }
}
