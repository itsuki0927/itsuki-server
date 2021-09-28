package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.SystemConfig;
import cn.itsuki.blog.entities.responses.WrapperResponse;
import cn.itsuki.blog.services.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author: itsuki
 * @create: 2021-09-28 08:29
 **/
@RestController
@RequestMapping("/config")
public class SystemConfigController {

    @Autowired
    private SystemConfigService service;

    @GetMapping
    public WrapperResponse<SystemConfig> get() {
        return WrapperResponse.build(service.get(1));
    }

    @PutMapping
    public WrapperResponse<SystemConfig> update(@Valid @RequestBody SystemConfig entity) {
        return WrapperResponse.build(service.update(entity.getId(), entity));
    }
}
