package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.responses.SiteInfoResponse;
import cn.itsuki.blog.entities.responses.SiteSummaryResponse;
import cn.itsuki.blog.entities.responses.WrapperResponse;
import cn.itsuki.blog.services.SiteInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 网站信息 控制器
 *
 * @author: itsuki
 * @create: 2021-10-24 18:13
 **/
@RestController
@RequestMapping("site-info")
public class SiteInfoController {
    @Autowired
    private SiteInfoService service;

    @GetMapping
    public WrapperResponse<SiteInfoResponse> get() {
        return WrapperResponse.build(service.get(), "请求成功");
    }

    @GetMapping("/summary")
    public WrapperResponse<SiteSummaryResponse> count() {
        return WrapperResponse.build(service.getSummary(), "请求成功");
    }
}
