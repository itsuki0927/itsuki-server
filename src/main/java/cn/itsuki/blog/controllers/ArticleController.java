package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.Article;
import cn.itsuki.blog.entities.ArticleArchive;
import cn.itsuki.blog.entities.ArticleId;
import cn.itsuki.blog.entities.Comment;
import cn.itsuki.blog.entities.requests.ArticleCreateRequest;
import cn.itsuki.blog.entities.requests.ArticleMetaPatchRequest;
import cn.itsuki.blog.entities.requests.ArticlePatchRequest;
import cn.itsuki.blog.entities.requests.ArticleSearchRequest;
import cn.itsuki.blog.entities.responses.ArticleDetailResponse;
import cn.itsuki.blog.entities.responses.ArticleSummaryResponse;
import cn.itsuki.blog.entities.responses.SearchResponse;
import cn.itsuki.blog.entities.responses.WrapperResponse;
import cn.itsuki.blog.services.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.TreeMap;

/**
 * 文章 控制器
 *
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

    @GetMapping("/paths")
    public WrapperResponse<List<ArticleId>> getPaths() {
        return WrapperResponse.build(service.getPaths());
    }

    @GetMapping("/{id}/comments")
    public WrapperResponse<List<Comment>> getComments(@PathVariable("id") Long articleId) {
        return WrapperResponse.build(service.getComments(articleId));
    }

    @GetMapping("/summary")
    public WrapperResponse<ArticleSummaryResponse> getSummary() {
        return WrapperResponse.build(service.getSummary());
    }

    @GetMapping("/archive")
    public WrapperResponse<TreeMap<String, TreeMap<String, List<ArticleArchive>>>> archive() {
        return WrapperResponse.build(service.getArchive());
    }

    @GetMapping("/{id}")
    public WrapperResponse<ArticleDetailResponse> get(@PathVariable("id") Long id) {
        return WrapperResponse.build(service.get(id));
    }

    @GetMapping
    public WrapperResponse<SearchResponse<Article>> search(@Valid @ModelAttribute ArticleSearchRequest criteria) {
        return WrapperResponse.build(service.search(criteria));
    }

    @GetMapping("/count")
    public WrapperResponse<Integer> count(@Valid @ModelAttribute ArticleSearchRequest criteria) {
        return WrapperResponse.build(service.count(criteria));
    }

    @PutMapping("/{id}")
    public WrapperResponse<Article> put(@PathVariable("id") Long id, @Valid @RequestBody ArticleCreateRequest article) {
        return WrapperResponse.build(service.update(id, article));
    }

    @DeleteMapping("/{id}")
    public WrapperResponse<Integer> delete(@PathVariable("id") Long id) {
        return WrapperResponse.build(service.delete(id));
    }

    @PatchMapping("/{id}")
    public WrapperResponse<Integer> patch(@PathVariable("id") Long id, @Valid @RequestBody ArticleMetaPatchRequest request) {
        return WrapperResponse.build(service.patchMeta(id, request));
    }

    @PatchMapping("/{id}/like")
    public WrapperResponse<Integer> patchLike(@PathVariable("id") Long id) {
        return WrapperResponse.build(service.patchLike(id));
    }

    @PatchMapping("/{id}/read")
    public WrapperResponse<Integer> patchRead(@PathVariable("id") Long id) {
        return WrapperResponse.build(service.patchRead(id));
    }

    @PatchMapping
    public WrapperResponse<Integer> patch(@Valid @RequestBody ArticlePatchRequest request) {
        return WrapperResponse.build(service.patch(request));
    }
}
