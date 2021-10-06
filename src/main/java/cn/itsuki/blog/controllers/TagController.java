package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.Tag;
import cn.itsuki.blog.entities.requests.TagSearchRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 标签 控制器
 *
 * @author: itsuki
 * @create: 2021-09-21 18:16
 **/
@RestController
@RequestMapping("tag")
public class TagController extends BaseController<Tag, TagSearchRequest> {

}
