package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.responses.WrapperResponse;
import cn.itsuki.blog.services.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传 控制器
 *
 * @author: itsuki
 * @create: 2021-09-27 22:58
 **/
@RestController
@RequestMapping("/upload")
public class UploadController {
    @Autowired
    protected UploadService service;

    @PostMapping("/file")
    public WrapperResponse<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("prefix") String prefix) {
        return WrapperResponse.build(this.service.uploadFile(prefix, file), "上传成功");
    }
}
