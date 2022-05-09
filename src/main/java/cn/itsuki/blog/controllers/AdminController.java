package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.Admin;
import cn.itsuki.blog.entities.responses.WrapperResponse;
import cn.itsuki.blog.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/current-admin")
    public WrapperResponse<Admin> getCurrentAdmin() {
        return WrapperResponse.build(adminService.getCurrentAdmin(), "获取信息成功");
    }
}
