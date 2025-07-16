package com.example.demo.utils;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.json.JSONObject;
import org.json.JSONArray;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MainAllInOne {
    public static void main(String[] args) throws Exception {
        // 1. 获取access_token（百度API调用凭证）
        String accessToken = BaiduAuth.getAccessToken();
        String groupId = "test_group";
        System.out.println("Access Token: " + accessToken);

        // 2. 注册photos目录下所有用户文件夹下的所有图片到百度人脸库
        File photosDir = new File("photos");
        if (photosDir.exists() && photosDir.isDirectory()) {
            for (File userDir : photosDir.listFiles()) {
                if (userDir.isDirectory()) {
                    // user_id 只去除特殊符号，保留中英文、数字、下划线
                    String rawUserId = userDir.getName();
                    String userId = rawUserId.replaceAll("[^\u4e00-\u9fa5a-zA-Z0-9_]", "");
                    if (!userId.equals(rawUserId)) {
                        System.out.println("[WARN] user_id '" + rawUserId + "' contains special chars, will use '" + userId + "' for registration.");
                    }
                    for (File img : userDir.listFiles()) {
                        if (img.isFile() && (img.getName().endsWith(".jpg") || img.getName().endsWith(".png"))) {
                            System.out.println("Registering: " + img.getAbsolutePath());
                            // 注册人脸到百度人脸库
                            BaiduFaceRegister.registerFace(accessToken, groupId, userId, img.getAbsolutePath());
                            Thread.sleep(1000); // 避免QPS超限
                        }
                    }
                }
            }
        } else {
            System.out.println("No photos directory found.");
        }

        // 3. 自动进入摄像头实时识别
        System.out.println("Starting realtime camera recognition...");
        // 加载OpenCV人脸检测分类器
        String classifierPath = "haarcascade_frontalface_alt.xml";
        CascadeClassifier faceDetector = new CascadeClassifier(classifierPath);
        if (faceDetector.empty()) {
            JOptionPane.showMessageDialog(null, "Face classifier file not found: " + classifierPath);
            return;
        }
        // 打开摄像头
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();
        CanvasFrame canvas = new CanvasFrame("Camera Realtime Recognition - ESC to exit", 1);
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Java2DFrameConverter converter = new Java2DFrameConverter();
        OpenCVFrameConverter.ToMat toMat = new OpenCVFrameConverter.ToMat();
        String lastResult = "";
        long lastRecognizeTime = 0;
        long recognizeInterval = 1000; // 1秒识别一次
        while (canvas.isVisible()) {
            // 采集摄像头帧
            org.bytedeco.javacv.Frame frame = grabber.grab();
            Mat mat = toMat.convert(frame);
            if (mat == null) continue;
            // 人脸检测
            RectVector faces = new RectVector();
            faceDetector.detectMultiScale(mat, faces);
            // 画人脸框
            for (int i = 0; i < faces.size(); i++) {
                Rect face = faces.get(i);
                opencv_imgproc.rectangle(mat, face, new Scalar(0, 255, 0, 1));
            }
            long now = System.currentTimeMillis();
            // 实时识别（每1秒一次）
            if (faces.size() > 0 && now - lastRecognizeTime > recognizeInterval) {
                // 临时保存当前帧为图片
                String tempFile = "temp_recognize.jpg";
                opencv_imgcodecs.imwrite(tempFile, mat);
                try {
                    // 调用百度API识别人脸
                    String result = recognizeFaceBaidu(accessToken, groupId, tempFile);
                    lastResult = result;
                } catch (Exception e) {
                    lastResult = "Recognition error: " + e.getMessage();
                }
                lastRecognizeTime = now;
                new File(tempFile).delete();
            }
            // 用Java AWT在画面上绘制识别结果（支持中文）
            BufferedImage bufferedImage = converter.getBufferedImage(toMat.convert(mat));
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.setFont(new Font("微软雅黑", Font.BOLD, 24));
            g2d.setColor(new Color(255, 255, 0));
            if (!lastResult.isEmpty()) {
                g2d.drawString(lastResult, 30, 40);
            }
            g2d.setFont(new Font("微软雅黑", Font.PLAIN, 20));
            g2d.setColor(new Color(255,255,255));
            g2d.drawString("ESC to exit", 30, 80);
            g2d.dispose();
            org.bytedeco.javacv.Frame showFrame = converter.convert(bufferedImage);
            canvas.showImage(showFrame);
            // 监听ESC键退出
            KeyEvent keyEvent = canvas.waitKey(33);
            if (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                break;
            }
        }
        // 释放资源
        canvas.dispose();
        grabber.stop();
        System.out.println("Program ended.");
    }

    /**
     * 调用百度API识别人脸，返回用户名和分数
     * @param accessToken 百度access_token
     * @param groupId 人脸库分组
     * @param imagePath 待识别图片路径
     * @return 识别结果字符串
     */
    public static String recognizeFaceBaidu(String accessToken, String groupId, String imagePath) throws Exception {
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/search?access_token=" + accessToken;
        String imgStr = Base64.getEncoder().encodeToString(Files.readAllBytes(new File(imagePath).toPath()));
        JSONObject param = new JSONObject();
        param.put("image", imgStr);
        param.put("image_type", "BASE64");
        param.put("group_id_list", groupId);
        // 发送HTTP POST请求到百度API
        String response = org.apache.http.client.fluent.Request.Post(url)
                .bodyString(param.toString(), org.apache.http.entity.ContentType.APPLICATION_JSON)
                .addHeader("Content-Type", "application/json")
                .execute().returnContent().asString();
        JSONObject json = new JSONObject(response);
        // 解析返回结果，提取user_id和分数
        if (json.has("result") && json.getJSONObject("result").has("user_list")) {
            JSONArray userList = json.getJSONObject("result").getJSONArray("user_list");
            if (userList.length() > 0) {
                JSONObject user = userList.getJSONObject(0);
                String userId = user.getString("user_id");
                double score = user.getDouble("score");
                // 这里可以返回中文user_id
                return "用户: " + userId + ", 分数: " + String.format("%.2f", score);
            }
        }
        return "未匹配到用户";
    }

    /**
     * 调用百度API识别人脸，返回用户名和分数（支持byte[]图片数据）
     * @param accessToken 百度access_token
     * @param groupId 人脸库分组
     * @param imageBytes 图片二进制数据
     * @return 识别结果字符串
     */
    public static String recognizeFaceBaidu(String accessToken, String groupId, byte[] imageBytes) throws Exception {
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/search?access_token=" + accessToken;
        String imgStr = Base64.getEncoder().encodeToString(imageBytes);
        JSONObject param = new JSONObject();
        param.put("image", imgStr);
        param.put("image_type", "BASE64");
        param.put("group_id_list", groupId);
        // 发送HTTP POST请求到百度API
        String response = org.apache.http.client.fluent.Request.Post(url)
                .bodyString(param.toString(), org.apache.http.entity.ContentType.APPLICATION_JSON)
                .addHeader("Content-Type", "application/json")
                .execute().returnContent().asString();
        JSONObject json = new JSONObject(response);
        // 解析返回结果，提取user_id和分数
        if (json.has("result") && json.getJSONObject("result").has("user_list")) {
            JSONArray userList = json.getJSONObject("result").getJSONArray("user_list");
            if (userList.length() > 0) {
                JSONObject user = userList.getJSONObject(0);
                String userId = user.getString("user_id");
                double score = user.getDouble("score");
                // 这里可以返回中文user_id
                return "用户: " + userId + ", 分数: " + String.format("%.2f", score);
            }
        }
        return "未匹配到用户";
    }
} 