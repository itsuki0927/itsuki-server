package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.Article;
import cn.itsuki.blog.entities.ArticleArchive;
import cn.itsuki.blog.entities.ArticleId;
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

    @GetMapping("/paths")
    public WrapperResponse<List<ArticleId>> getPaths() {
        return WrapperResponse.build(service.getPaths());
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
        return WrapperResponse.build(service.detail(id));
    }

    @GetMapping("/count")
    public WrapperResponse<Integer> count(@Valid @ModelAttribute ArticleSearchRequest criteria) {
        return WrapperResponse.build(service.count(criteria));
    }

    @PatchMapping("/{id}/read")
    public WrapperResponse<Integer> patchRead(@PathVariable("id") Long id) {
        return WrapperResponse.build(service.patchRead(id));
    }
}
