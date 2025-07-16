package com.example.demo.utils;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * 百度人脸注册API调用工具类
 * 用于将本地图片注册到百度人脸库
 */
public class BaiduFaceRegister {
    /**
     * 注册人脸到百度人脸库
     * @param accessToken 百度access_token
     * @param groupId 人脸库分组（如test_group）
     * @param userId 用户唯一标识（建议用英文/拼音/数字/下划线）
     * @param imagePath 本地图片路径
     * @throws Exception 网络或API异常
     */
    public static void registerFace(String accessToken, String groupId, String userId, String imagePath) throws Exception {
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add?access_token=" + accessToken;
        String imgStr = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(imagePath)));
        JSONObject param = new JSONObject();
        param.put("image", imgStr);
        param.put("image_type", "BASE64");
        param.put("group_id", groupId);
        param.put("user_id", userId);

        String response = Request.Post(url)
                .bodyString(param.toString(), ContentType.APPLICATION_JSON)
                .addHeader("Content-Type", "application/json")
                .execute().returnContent().asString();
        System.out.println("Register response: " + response);
    }
} 