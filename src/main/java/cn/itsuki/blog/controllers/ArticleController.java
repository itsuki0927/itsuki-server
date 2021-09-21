package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.Article;
import cn.itsuki.blog.entities.requests.ArticleSearchRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author: itsuki
 * @create: 2021-09-20 22:16
 **/
@RestController
@RequestMapping("/article")
public class ArticleController extends BaseController<Article, ArticleSearchRequest> {

    @PostMapping
    public Article create(@Valid @RequestBody Article article) {
        return service.create(article);
    }
}
