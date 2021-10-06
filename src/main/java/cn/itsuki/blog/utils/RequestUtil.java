package cn.itsuki.blog.utils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * 请求工具类
 *
 * @author: itsuki
 * @create: 2021-10-03 16:32
 **/
@Service
public class RequestUtil {
    @Value("${baidu.ip-url}")
    String bdUrl;
    @Value("${baidu.ip-ak}")
    String ak;

    /**
     * 获取请求真实IP地址
     */
    public String getRequestIp(HttpServletRequest request) {
        //通过HTTP代理服务器转发时添加
        String ipAddress = request.getHeader("x-forwarded-for");
        System.out.println(ipAddress);
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
            System.out.println(ipAddress);
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
            System.out.println(ipAddress);
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            System.out.println(ipAddress);
            // 从本地访问时根据网卡取本机配置的IP
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                InetAddress inetAddress = null;
                try {
                    inetAddress = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inetAddress.getHostAddress();
                System.out.println(ipAddress);
            }
        }
        // 通过多个代理转发的情况，第一个IP为客户端真实IP，多个IP会按照','分割
        if (ipAddress != null && ipAddress.length() > 15) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        System.out.println(ipAddress);
        return ipAddress;
    }

    /**
     * 读取
     */
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private String buildUrl(String ip) {
        return bdUrl + "?ak=" + ak + "&ip=" + ip + "&coor=bd09ll";
    }

    /**
     * 创建链接
     */
    private JSONObject readJsonFromUrl(String ip) throws IOException, JSONException {
        String url = buildUrl(ip);
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return JSONObject.parseObject(jsonText);
        }
    }

    /**
     * 通过Ip获取所在地
     *
     * @param ip ip
     * @return 所在地
     */
    public JSONObject findLocationByIp(String ip) {
        try {
            JSONObject json = readJsonFromUrl(ip);

            JSONObject obj = (JSONObject) ((JSONObject) json.get("content")).get("address_detail");

            return obj;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
