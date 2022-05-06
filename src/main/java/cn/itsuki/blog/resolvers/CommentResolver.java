package cn.itsuki.blog.resolvers;

import cn.itsuki.blog.entities.Comment;
import cn.itsuki.blog.repositories.CommentRepository;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: itsuki
 * @create: 2022-04-28 11:34
 **/
@Component
public class CommentResolver implements GraphQLQueryResolver {
    @Autowired
    private CommentRepository repository;

    public List<Comment> comments(Long articleId) {
        return repository.findAll();
    }
}
