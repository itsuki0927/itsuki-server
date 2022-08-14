package cn.itsuki.blog.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: itsuki
 * @create: 2022-05-08 22:00
 **/
@Component
public class UrlUtil {
    @Value("${cors.webUrl}")
    private String webUrl;

    public String getArticleUrl(String path) {
        return webUrl + "/blog/" + path;
    }

    public String getGuestBookUrl() {
        return webUrl + "/guestbook";
    }

    public String getTagUrl(String path) {
        return webUrl + "/tag/" + path;
    }
}
