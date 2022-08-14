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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
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
    @Value("${mode}")
    private String mode;
    @Value("${seo.google.type")
    private String type;
    @Value("${seo.google.projectId")
    private String projectId;
    @Value("${seo.google.privateKeyId")
    private String privateKeyId;
    @Value("${seo.google.privateKey")
    private String privateKey;
    @Value("${seo.google.clientEmail")
    private String clientEmail;
    @Value("${seo.google.clientId")
    private String clientId;
    @Value("${seo.google.authUri")
    private String authUri;
    @Value("${seo.google.tokenUri")
    private String tokenUri;
    @Value("${seo.google.authProviderCertUrl")
    private String authProviderCertUrl;
    @Value("${seo.google.clientCertUrl")
    private String clientCertUrl;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public boolean isProd() {
        return mode.equals("prod");
    }

    private String getBaiduActionString(SEOAction action) {
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

    private String getGoogleActionString(SEOAction action) {
        switch (action) {
            case Push:
            case Update:
                return "URL_UPDATED";
            case Delete:
                return "URL_DELETED";
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
        String type = getGoogleActionString(action);
        String actionText = "Google ping [" + type + "] action";
        String url = urls.get(0);
        String secretJson = buildGoogleSecretJson();

        System.out.println(actionText);

        try {
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            InputStream secretInput = new ByteArrayInputStream(secretJson.getBytes());
            System.out.println(secretInput.toString());
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
            System.out.println("ping google success");
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            logger.error("pingGoogle error:" + e.getMessage());
        }
    }

    private String buildGoogleSecretJson() {
        return "{"
                + "\"type\": \"" + type + "\","
                + "\"project_id\": \"" + projectId + "\","
                + "\"private_key_id\": \"" + privateKeyId + "\","
                + "\"private_key\": \"" + privateKey + "\","
                + "\"client_email\": \"" + clientEmail + "\","
                + "\"client_id\": \"" + clientId + "\","
                + "\"auth_uri\": \"" + authUri + "\","
                + "\"token_uri\": \"" + tokenUri + "\","
                + "\"auth_provider_x509_cert_url\": \"" + authProviderCertUrl + "\","
                + "\"client_x509_cert_url\": \"" + clientCertUrl + "\""
                + "}";
    }

    // Baidu
    private void pingBaidu(SEOAction action, List<String> urls) {

        String urlKey = getBaiduActionString(action);
        String actionText = "Baidu ping [" + action + "] action";
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

        try {
            String result = HttpUtil.createPost(url)
                    .body(content, "text/plain")
                    .timeout(20000).execute().body();//超时，毫秒
            System.out.println(actionText + result);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("pingBaidu error:" + e.getMessage());
        }
    }

    public void push(String url) {
        if (isProd()) {
            List<String> urls = humanizedUrl(url);
            this.pingBaidu(SEOAction.Push, urls);
            this.pingGoogle(SEOAction.Push, urls);
        }
    }

    public void update(String url) {
        if (isProd()) {
            List<String> urls = humanizedUrl(url);
            this.pingBaidu(SEOAction.Update, urls);
            this.pingGoogle(SEOAction.Update, urls);
        }
    }

    public void delete(String url) {
        if (isProd()) {
            List<String> urls = humanizedUrl(url);
            this.pingBaidu(SEOAction.Delete, urls);
            this.pingGoogle(SEOAction.Delete, urls);
        }
    }
}
