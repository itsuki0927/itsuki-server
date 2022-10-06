package cn.itsuki.blog.resolvers;

import cn.itsuki.blog.entities.Blog;
import cn.itsuki.blog.entities.BlogTag;
import cn.itsuki.blog.entities.Tag;
import cn.itsuki.blog.repositories.BlogTagRepository;
import cn.itsuki.blog.repositories.TagRepository;
import graphql.kickstart.tools.GraphQLResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: itsuki
 * @create: 2022-04-27 13:32
 **/
@Slf4j
@Component
public class BlogResolver implements GraphQLResolver<Blog> {
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private BlogTagRepository blogTagRepository;

    public List<Tag> tags(Blog blog) {
        BlogTag probe = new BlogTag();
        probe.setBlogId(blog.getId());
        List<BlogTag> blogTags = blogTagRepository.findAll(Example.of(probe));
        return tagRepository.findAllById(blogTags.stream().map(BlogTag::getTagId).collect(Collectors.toList()));
    }

}
