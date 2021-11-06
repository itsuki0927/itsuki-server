package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.Article;
import cn.itsuki.blog.entities.Snippet;
import cn.itsuki.blog.entities.requests.*;
import cn.itsuki.blog.entities.responses.SearchResponse;
import cn.itsuki.blog.entities.responses.WrapperResponse;
import cn.itsuki.blog.services.ArticleService;
import cn.itsuki.blog.services.SnippetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 片段 控制器
 *
 * @author: itsuki
 * @create: 2021-11-05 13:57
 **/
@RequestMapping("/snippet")
@RestController
public class SnippetController {

    @Autowired
    SnippetService service;

    @GetMapping
    public WrapperResponse<SearchResponse<Snippet>> search(@Valid @ModelAttribute SnippetSearchRequest criteria) {
        return WrapperResponse.build(service.search(criteria), "搜索成功");
    }

    @GetMapping("/{id}")
    public WrapperResponse<Snippet> get(@PathVariable("id") Long id) {
        return WrapperResponse.build(service.get(id), "搜索成功");
    }

    @PutMapping("/{id}")
    public WrapperResponse<Snippet> put(@PathVariable("id") Long id, @Valid @RequestBody SnippetCreateRequest request) {
        return WrapperResponse.build(service.update(id, request));
    }

    @PostMapping
    public WrapperResponse<Snippet> create(@Valid @RequestBody SnippetCreateRequest request) {
        return WrapperResponse.build(service.create(request), "创建成功");
    }

    @DeleteMapping("/{id}")
    public WrapperResponse<Integer> delete(@PathVariable("id") Long id) {
        return WrapperResponse.build(service.delete(id));
    }

}
