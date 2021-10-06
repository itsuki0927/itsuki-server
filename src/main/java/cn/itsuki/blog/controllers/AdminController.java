package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.Admin;
import cn.itsuki.blog.entities.requests.AdminSaveRequest;
import cn.itsuki.blog.entities.requests.LoginRequest;
import cn.itsuki.blog.entities.responses.LoginResponse;
import cn.itsuki.blog.entities.responses.WrapperResponse;
import cn.itsuki.blog.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 管理员 控制器
 *
 * @author: itsuki
 * @create: 2021-09-15 19:54
 **/
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public WrapperResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return WrapperResponse.build(adminService.login(request), "登陆成功");
    }

    @GetMapping("/current-admin")
    public WrapperResponse<Admin> getCurrentAdmin() {
        return WrapperResponse.build(adminService.getCurrentAdmin(), "获取信息成功");
    }

    @PostMapping("/save")
    public WrapperResponse<Admin> save(@Valid @RequestBody AdminSaveRequest request) {
        return WrapperResponse.build(adminService.save(request), "保存成功");
    }
}
