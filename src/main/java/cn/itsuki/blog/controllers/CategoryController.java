package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.Category;
import cn.itsuki.blog.entities.requests.BaseSearchRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分类 controller
 *
 * @author: itsuki
 * @create: 2021-09-21 18:16
 **/
@RestController
@RequestMapping("category")
public class CategoryController extends BaseController<Category, BaseSearchRequest> {
}
