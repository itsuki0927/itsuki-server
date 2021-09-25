package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.Admin;
import cn.itsuki.blog.entities.requests.AdminSearchRequest;
import cn.itsuki.blog.entities.requests.LoginRequest;
import cn.itsuki.blog.entities.responses.LoginResponse;
import cn.itsuki.blog.entities.responses.WrapperResponse;
import cn.itsuki.blog.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * admin controller
 *
 * @author: itsuki
 * @create: 2021-09-15 19:54
 **/
@RestController
@RequestMapping("/admin")
public class AdminController extends BaseController<Admin, AdminSearchRequest> {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public WrapperResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return WrapperResponse.build(adminService.login(request), "登陆成功");
    }

    @GetMapping("/current-user")
    public WrapperResponse<Admin> getCurrentUser() {
        return WrapperResponse.build(adminService.getCurrentAdmin(), "获取信息成功");
    }
}