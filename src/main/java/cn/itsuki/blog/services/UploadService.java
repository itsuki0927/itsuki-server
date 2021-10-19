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
 * 上传 服务
 *
 * @author: itsuki
 * @create: 2021-09-24 13:01
 **/
@Service
public class UploadService {
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
    private Configuration cfg = new Configuration(Region.region0());

    private UploadManager uploadManager = new UploadManager(cfg);

    public String uploadFile(String prefix, MultipartFile file) {
        Auth auth = Auth.create(accessKey, secretKey);
        String token = auth.uploadToken(bucket);
        try {
            String originalFilename = file.getOriginalFilename();
            String path = originalFilename;
            if (prefix != null) {
                path = prefix + "/" + originalFilename;
            }
            uploadManager.put(file.getInputStream(), path, token, null, null);
            return path;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }
}
