package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.requests.SystemSettingsRequest;
import cn.itsuki.blog.entities.responses.SystemSettingsResponse;
import cn.itsuki.blog.entities.responses.WrapperResponse;
import cn.itsuki.blog.services.SystemSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 系统设置 控制器
 *
 * @author: itsuki
 * @create: 2021-09-28 08:29
 **/
@RestController
@RequestMapping("/config")
public class SystemSettingsController {

    @Autowired
    private SystemSettingsService service;

    @GetMapping
    public WrapperResponse<SystemSettingsResponse> get() {
        return WrapperResponse.build(service.get());
    }

    @PutMapping
    public WrapperResponse<SystemSettingsResponse> update(@Valid @RequestBody SystemSettingsRequest entity) {
        return WrapperResponse.build(service.update(entity));
    }
}
