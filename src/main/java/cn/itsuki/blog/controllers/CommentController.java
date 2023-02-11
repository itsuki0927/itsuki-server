package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.Comment;
import cn.itsuki.blog.entities.requests.AdminCommentInput;
import cn.itsuki.blog.entities.requests.CommentCreateRequest;
import cn.itsuki.blog.entities.requests.SearchCommentInput;
import cn.itsuki.blog.entities.requests.UpdateCommentInput;
import cn.itsuki.blog.entities.responses.SearchResponse;
import cn.itsuki.blog.services.CommentService;
import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;

    @QueryMapping
    public SearchResponse<Comment> comments(@Argument SearchCommentInput input) {
        return this.commentService.comments(input);
    }

    @QueryMapping
    public Comment comment(@Argument Long id) {
        return this.commentService.comment(id);
    }

    @Secured("ROLE_ADMIN")
    @QueryMapping
    public Comment adminComment(@Argument AdminCommentInput input) {
        return this.commentService.adminComment(input);
    }

    @MutationMapping
    public Comment createComment(@Argument CommentCreateRequest input, GraphQLContext context) {
        return this.commentService.createComment(input, context);
    }

    @Secured("ROLE_ADMIN")
    @MutationMapping
    public int deleteComment(@Argument Long id) {
        return this.commentService.deleteComment(id);
    }

    @MutationMapping
    public String likeComment(@Argument Long id, @Argument String emoji) {
        return this.commentService.likeComment(id, emoji);
    }

    @Secured("ROLE_ADMIN")
    @MutationMapping
    public int updateCommentState(@Argument Long id, @Argument Integer state) {
        return this.commentService.updateCommentState(id, state);
    }

    @Secured("ROLE_ADMIN")
    @MutationMapping
    public Comment updateComment(@Argument Long id, @Argument UpdateCommentInput input) {
        return this.commentService.updateComment(id, input);
    }
}
