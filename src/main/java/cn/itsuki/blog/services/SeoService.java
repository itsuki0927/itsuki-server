package cn.itsuki.blog.services;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.itsuki.blog.constants.SEOAction;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.*;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/**
 * @author: itsuki
 * @create: 2022-05-09 15:07
 **/
@Service
public class SeoService {
    @Value("${seo.baidu.site}")
    private String site;
    @Value("${seo.baidu.token}")
    private String token;
    @Value("${mode.isProd}")
    private boolean isProd;

    private String getActionString(SEOAction action) {
        switch (action) {
            case Push:
                return "urls";
            case Update:
                return "update";
            case Delete:
                return "del";
            default:
                return "";
        }
    }

    private List<String> humanizedUrl(String url) {
        return List.of(url);
    }

    private void pingGoogle(SEOAction action, List<String> urls) {
        String scopes = "https://www.googleapis.com/auth/indexing";
        String endPoint = "https://indexing.googleapis.com/v3/urlNotifications:publish";
        String type = getActionString(action);
        String actionText = "Baidu ping[ " + type + " ] action";
        String url = urls.get(0);

        System.out.println(actionText);

        try {
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            InputStream secretInput = getClass().getResourceAsStream("/google_secret.json");
            GoogleCredential credentials = GoogleCredential.fromStream(secretInput, httpTransport, jsonFactory).createScoped(Collections.singleton(scopes));

            GenericUrl genericUrl = new GenericUrl(endPoint);
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();

            String content = "{"
                    + "\"url\": \"" + url + "\","
                    + "\"type\": \"" + type + "\","
                    + "}";
            HttpRequest request =
                    requestFactory.buildPostRequest(genericUrl, ByteArrayContent.fromString("application/json", content));
            credentials.initialize(request);
            HttpResponse response = request.execute();

            System.out.println(IoUtil.read(response.getContent()).toString());
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    // Baidu
    private void pingBaidu(SEOAction action, List<String> urls) {

        String urlKey = getActionString(action);
        String actionText = "Baidu ping[ " + action + " ] action";
        String content = String.join("\n", urls);

        if (StrUtil.isEmpty(urlKey)) {
            return;
        }

        String url = StrUtil.builder()
                .append("http://data.zz.baidu.com/")
                .append(urlKey)
                .append("?site=")
                .append(site)
                .append("&token=")
                .append(token).toString();

        String result = HttpUtil.createPost(url)
                .body(content, "text/plain")
                .timeout(20000).execute().body();//超时，毫秒

        System.out.println(actionText + result);
    }

    public void push(String url) {
        if (isProd) {
            List<String> urls = humanizedUrl(url);
            this.pingBaidu(SEOAction.Push, urls);
            this.pingGoogle(SEOAction.Push, urls);
        }
    }

    public void update(String url) {
        if (isProd) {
            List<String> urls = humanizedUrl(url);
            this.pingBaidu(SEOAction.Update, urls);
            this.pingGoogle(SEOAction.Update, urls);
        }
    }

    public void delete(String url) {
        if (isProd) {
            List<String> urls = humanizedUrl(url);
            this.pingBaidu(SEOAction.Delete, urls);
            this.pingGoogle(SEOAction.Delete, urls);
        }
    }
}
