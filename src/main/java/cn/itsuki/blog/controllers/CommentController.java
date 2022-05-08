package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.Comment;
import cn.itsuki.blog.entities.requests.*;
import cn.itsuki.blog.entities.responses.SearchResponse;
import cn.itsuki.blog.entities.responses.WrapperResponse;
import cn.itsuki.blog.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 评论 控制器
 *
 * @author: itsuki
 * @create: 2021-10-03 16:33
 **/
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService service;

    @GetMapping("/count/{articleId}")
    public WrapperResponse<Integer> count(@PathVariable("articleId") Long articleId) {
        return WrapperResponse.build(service.count(articleId));
    }

    @PatchMapping("/{id}/like")
    public WrapperResponse<Integer> patchLike(@PathVariable("id") Long id) {
        return WrapperResponse.build(service.patchLike(id));
    }
}
