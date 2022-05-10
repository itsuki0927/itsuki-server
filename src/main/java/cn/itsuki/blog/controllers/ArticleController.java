package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.ArticleArchive;
import cn.itsuki.blog.entities.ArticleId;
import cn.itsuki.blog.entities.requests.ArticleSearchRequest;
import cn.itsuki.blog.entities.responses.ArticleSummaryResponse;
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

    @GetMapping("/summary")
    public WrapperResponse<ArticleSummaryResponse> getSummary() {
        return WrapperResponse.build(service.getSummary());
    }
}
