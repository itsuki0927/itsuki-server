package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.Article;
import cn.itsuki.blog.entities.requests.ArticleCreateRequest;
import cn.itsuki.blog.entities.requests.ArticlePatchRequest;
import cn.itsuki.blog.entities.requests.ArticleSearchRequest;
import cn.itsuki.blog.entities.responses.SearchResponse;
import cn.itsuki.blog.entities.responses.WrapperResponse;
import cn.itsuki.blog.services.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author: itsuki
 * @create: 2021-09-20 22:16
 **/
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    ArticleService service;

    @PostMapping
    public WrapperResponse<Article> create(@Valid @RequestBody ArticleCreateRequest article) {
        return WrapperResponse.build(service.create(article), "创建成功");
    }

    @GetMapping
    public WrapperResponse<SearchResponse<Article>> search(@Valid @ModelAttribute ArticleSearchRequest criteria) {
        return WrapperResponse.build(service.search(criteria));
    }

    @GetMapping("/{id}")
    public WrapperResponse<Article> get(@PathVariable("id") Long id) {
        return WrapperResponse.build(service.get(id));
    }

    @PutMapping("/{id}")
    public WrapperResponse<Article> put(@PathVariable("id") Long id, @Valid @RequestBody ArticleCreateRequest article) {
        return WrapperResponse.build(service.update(id, article));
    }

    @DeleteMapping("/{id}")
    public WrapperResponse<Integer> delete(@PathVariable("id") Long id) {
        return WrapperResponse.build(service.delete(id));
    }

    @PatchMapping
    public WrapperResponse<Integer> patch(@Valid @RequestBody ArticlePatchRequest request){
       return WrapperResponse.build(service.patch(request)) ;
    }
}
