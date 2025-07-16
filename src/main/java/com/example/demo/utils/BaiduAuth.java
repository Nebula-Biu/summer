package com.example.demo.utils;
import org.apache.http.client.fluent.Request;
import org.json.JSONObject;

/**
 * 百度API认证工具类
 * 用于获取百度人脸识别API的access_token
 */
public class BaiduAuth {
    /**
     * 获取百度access_token
     * @return access_token字符串
     * @throws Exception 网络或API异常
     */
    public static String getAccessToken() throws Exception {
        String apiKey = "iZ3qdodYvoNAHspQqjD1OdYN";
        String secretKey = "yir0eaFqZRQRTPD5oQBuTsKOwVgXbkfw";
        String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials"
                + "&client_id=" + apiKey
                + "&client_secret=" + secretKey;
        String result = Request.Get(url).execute().returnContent().asString();
        JSONObject json = new JSONObject(result);
        return json.getString("access_token");
    }
} 