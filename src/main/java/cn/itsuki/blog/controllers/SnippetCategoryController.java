package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.SnippetCategory;
import cn.itsuki.blog.entities.requests.SnippetCategorySearchRequest;
import cn.itsuki.blog.services.SnippetCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 片段分类 控制器
 *
 * @author: itsuki
 * @create: 2021-09-21 18:16
 **/
@RestController
@RequestMapping("snippet-category")
public class SnippetCategoryController extends BaseController<SnippetCategory, SnippetCategorySearchRequest> {
    @Autowired
    private SnippetCategoryService service;
}
