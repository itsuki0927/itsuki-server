package cn.itsuki.blog.services;

import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 上传service
 *
 * @author: itsuki
 * @create: 2021-09-24 13:01
 **/
@Service
public class UploadService {
    /**
     * 域名
     */
    @Value("${qiniu.domain}")
    private String domain;
    /**
     * 公钥
     */
    @Value("${qiniu.access-key}")
    private String accessKey;
    /**
     * 私钥
     */
    @Value("${qiniu.secret-key}")
    private String secretKey;
    /**
     * 存储空间名
     */
    @Value("${qiniu.bucket}")
    private String bucket;

    //构造一个带指定Region对象的配置类
    private Configuration cfg = new Configuration(Region.region2());

    private UploadManager uploadManager = new UploadManager(cfg);

    public String uploadFile(MultipartFile file) {
        Auth auth = Auth.create(accessKey, secretKey);
        String token = auth.uploadToken(bucket);
        try {
            String originalFilename = file.getOriginalFilename();
            uploadManager.put(file.getInputStream(), originalFilename, token, null, null);
            return domain + "/" + originalFilename;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }
}
