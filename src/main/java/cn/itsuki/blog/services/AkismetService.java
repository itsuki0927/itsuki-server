package cn.itsuki.blog.services;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.itsuki.blog.entities.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * akismet 服务
 *
 * @author: itsuki
 * @create: 2021-10-05 14:38
 **/
@Service
public class AkismetService {
    @Value("${akismet.secret-key}")
    private String secretKey;
    @Value("${akismet.blog}")
    private String blog;
    @Value("${akismet.hostname}")
    private String hostname;
    @Value("${mode}")
    private String mode;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public boolean isDev() {
        return mode.equals("dev");
    }

    private String buildPath(String path) {
        return "https://" + hostname + path;
    }

    private String buildPath(String prefix, String path) {
        return "https://" + prefix + "." + hostname + path;
    }

    // HTTP POST请求
    public void verifyKey() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("key", secretKey);
        paramMap.put("blog", blog);
        String url = buildPath("verify-key");
        String result = HttpUtil.createPost(url)
                .form(paramMap)//表单内容
                .timeout(20000)//超时，毫秒
                .execute().body();

        boolean invalid = !StrUtil.equals(result, "valid");

        if (invalid) {
            throw new IllegalArgumentException("verify key failed");
        }
    }

    private Map<String, Object> getRequestParams(Comment comment, boolean isAdmin) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("blog", blog);
        paramMap.put("user_ip", comment.getIp());
        paramMap.put("user_agent", comment.getAgent());
        paramMap.put("referrer", "127.0.0.1");
        paramMap.put("comment_type", "comment");
        paramMap.put("comment_author", comment.getNickname());
        paramMap.put("comment_author_email", comment.getEmail());
//        paramMap.put("comment_author_url", comment.getWebsite());
        paramMap.put("comment_content", comment.getContent());

        if (isDev()) {
            paramMap.put("is_test", true);
        }

        if (isAdmin) {
            paramMap.put("user_role", "administrator");
        }
        return paramMap;
    }

    // 检查评论
    public void checkComment(Comment comment, boolean isAdmin) {
        Map<String, Object> paramMap = getRequestParams(comment, isAdmin);
        boolean isSpam = false;

        try {
            String url = buildPath(secretKey, "comment-check");
            String result = HttpUtil.createPost(url)
                    .form(paramMap)//表单内容
                    .timeout(20000)//超时，毫秒
                    .execute().body();
            isSpam = StrUtil.equals(result, "true");
        } catch (Exception e) {
            System.out.println("Akismet 请求错误");
            System.out.println(e.getMessage());
        }
        if (isSpam) {
            throw new IllegalArgumentException("被 Akismet 过滤!!!");
        }
    }

    // 本应该是垃圾评论但是未标记未垃圾评论的
    public void submitSpam(Comment comment) {
        Map<String, Object> paramMap = getRequestParams(comment, false);

        try {
            String url = buildPath(secretKey, "submit-spam");
            String result = HttpUtil.createPost(url)
                    .form(paramMap)//表单内容
                    .timeout(20000)//超时，毫秒
                    .execute().body();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("submitSpam error:" + e.getMessage());
        }
    }


    // 本应该不是垃圾评论但是标记成垃圾评论的
    public void submitHam(Comment comment) {
        Map<String, Object> paramMap = getRequestParams(comment, false);

        try {
            String url = buildPath(secretKey, "submit-ham");
            String result = HttpUtil.createPost(url)
                    .form(paramMap)//表单内容
                    .timeout(20000)//超时，毫秒
                    .execute().body();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("submitHam error:" + e.getMessage());
        }
    }

}
