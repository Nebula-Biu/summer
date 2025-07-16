package com.example.demo.utils;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * 百度人脸识别API调用工具类
 * 用于将本地图片与人脸库比对
 */
public class BaiduFaceSearch {
    /**
     * 识别人脸（比对图片与百度人脸库）
     * @param accessToken 百度access_token
     * @param groupId 人脸库分组（如test_group）
     * @param imagePath 本地图片路径
     * @throws Exception 网络或API异常
     */
    public static void searchFace(String accessToken, String groupId, String imagePath) throws Exception {
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/search?access_token=" + accessToken;
        String imgStr = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(imagePath)));
        JSONObject param = new JSONObject();
        param.put("image", imgStr);
        param.put("image_type", "BASE64");
        param.put("group_id_list", groupId);

        String response = Request.Post(url)
                .bodyString(param.toString(), ContentType.APPLICATION_JSON)
                .addHeader("Content-Type", "application/json")
                .execute().returnContent().asString();
        System.out.println("Search response: " + response);
    }
} 