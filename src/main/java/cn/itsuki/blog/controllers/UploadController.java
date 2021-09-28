package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.responses.WrapperResponse;
import cn.itsuki.blog.services.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传 controller
 *
 * @author: itsuki
 * @create: 2021-09-27 22:58
 **/
@RestController
@RequestMapping("/upload")
public class UploadController {
    @Autowired
    protected UploadService service;

    @PostMapping("file")
    private WrapperResponse<String> uploadFile(MultipartFile file) {
        return WrapperResponse.build(this.service.uploadFile(file), "上传成功");
    }
}
