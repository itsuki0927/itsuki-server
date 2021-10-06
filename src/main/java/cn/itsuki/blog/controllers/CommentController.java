package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.Comment;
import cn.itsuki.blog.entities.requests.*;
import cn.itsuki.blog.entities.responses.SearchResponse;
import cn.itsuki.blog.entities.responses.WrapperResponse;
import cn.itsuki.blog.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 评论 controller
 *
 * @author: itsuki
 * @create: 2021-10-03 16:33
 **/
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService service;

    @PostMapping
    public WrapperResponse<Comment> create(@RequestBody @Valid CommentCreateRequest request) {
        return WrapperResponse.build(service.create(request));
    }

    @GetMapping("/count/{articleId}")
    public WrapperResponse<Integer> count(@PathVariable("articleId") Long articleId) {
        return WrapperResponse.build(service.count(articleId));
    }

    @GetMapping("/{articleId}")
    public WrapperResponse<List<Comment>> get(@PathVariable("articleId") Long articleId) {
        return WrapperResponse.build(service.get(articleId));
    }

    @GetMapping
    public WrapperResponse<SearchResponse<Comment>> search(@Valid @ModelAttribute CommentSearchRequest criteria) {
        return WrapperResponse.build(service.search(criteria));
    }

    @PutMapping("/{id}")
    public WrapperResponse<Comment> update(@PathVariable("id") Long id, @RequestBody @Valid CommentUpdateRequest request) {
        return WrapperResponse.build(service.update(id, request));
    }


    @PatchMapping
    public WrapperResponse<Integer> patch(@Valid @RequestBody CommentPatchRequest request) {
        return WrapperResponse.build(service.patch(request));
    }

    @PatchMapping("/{id}")
    public WrapperResponse<Integer> patch(@PathVariable("id") Long id, @Valid @RequestBody CommentMetaPatchRequest request) {
        return WrapperResponse.build(service.patchMeta(id, request));
    }

    @DeleteMapping("/{id}")
    public WrapperResponse<Integer> delete(@PathVariable("id") Long id) {
        return WrapperResponse.build(service.delete(id));
    }
}
