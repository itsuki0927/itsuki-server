package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.Article;
import cn.itsuki.blog.entities.requests.ArticleCreateRequest;
import cn.itsuki.blog.services.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ArticleController {

    @Autowired
    ArticleService service;

    @PostMapping
    public Article create(@Valid @RequestBody ArticleCreateRequest article) {
        return service.create(article);
    }
}
