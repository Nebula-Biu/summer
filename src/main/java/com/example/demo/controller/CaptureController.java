package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.example.demo.utils.BaiduAuth;
import com.example.demo.utils.BaiduFaceRegister;
import com.example.demo.utils.MainAllInOne;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.mapper.PersonMapper;
import com.example.demo.mapper.PersonDetailMapper;
import com.example.demo.mapper.FaceDataMapper;
import com.example.demo.mapper.AccessLogMapper;
import com.example.demo.mapper.AccessDeviceMapper;
import com.example.demo.entity.Person;
import com.example.demo.entity.PersonDetail;
import com.example.demo.entity.FaceData;
import com.example.demo.entity.AccessLog;
import com.example.demo.utils.SnowFlake;
import java.time.LocalDateTime;
import com.example.demo.utils.MacUtil;
import com.example.demo.entity.AccessDevice;

/**
 * 人脸注册与识别相关接口控制器
 * 包含实时识别、注册、查询、删除等API
 */
@Tag(name = "人脸注册与识别", description = "人脸注册与识别相关接口")
@RestController
@RequestMapping("/api")
public class CaptureController {
    private static final String GROUP_ID = "test_group";
    private static final String CLASSIFIER_PATH = "haarcascade_frontalface_alt.xml";

    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private PersonDetailMapper personDetailMapper;
    @Autowired
    private FaceDataMapper faceDataMapper;
    @Autowired
    private AccessLogMapper accessLogMapper;
    @Autowired
    private AccessDeviceMapper accessDeviceMapper;


    @Operation(summary = "base64图片实时识别（Web前端）", description = "前端传base64图片做识别，适合Web页面。", 
        responses = @ApiResponse(responseCode = "200", description = "识别结果", content = @Content(schema = @Schema(implementation = Map.class))))
    @PostMapping("/realtimeRecognizeByImage")
    public ResponseEntity<?> realtimeRecognizeByImage(@RequestBody Map<String, String> payload) {
        Map<String, Object> result = new HashMap<>();
        try {
            String base64Image = payload.get("image");
            if (base64Image == null || base64Image.isEmpty()) {
                result.put("code", 1);
                result.put("msg", "未提供图片");
                return ResponseEntity.ok(result);
            }
            String accessToken = BaiduAuth.getAccessToken();
            String pureBase64 = base64Image.contains(",") ? base64Image.substring(base64Image.indexOf(",") + 1) : base64Image;
            byte[] imageBytes = java.util.Base64.getDecoder().decode(pureBase64);
            // 临时保存图片
            String tempDir = "photos/realtime";
            File dir = new File(tempDir);
            if (!dir.exists()) dir.mkdirs();
            String fileName = "realtime_" + System.currentTimeMillis() + ".jpg";
            File saveFile = new File(dir, fileName);
            java.nio.file.Files.write(saveFile.toPath(), imageBytes);
            // 直接传递byte[]给百度API，不保存到本地
            String baiduResult = MainAllInOne.recognizeFaceBaidu(accessToken, GROUP_ID, imageBytes);
            boolean recognized = baiduResult != null && baiduResult.contains("用户:");
            String userId = null;
            Double score = null;
            String name = null;
            if (recognized) {
                int idx1 = baiduResult.indexOf("用户:");
                int idx2 = baiduResult.indexOf(", 分数:");
                if (idx1 >= 0 && idx2 > idx1) {
                    userId = baiduResult.substring(idx1 + 3, idx2).trim();
                    try {
                        score = Double.parseDouble(baiduResult.substring(idx2 + 5).trim());
                    } catch (Exception ignore) {}
                }
                if (userId != null) {
                    try {
                        Long personId = Long.parseLong(userId);
                        PersonDetail detail = personDetailMapper.selectByPersonId(personId);
                        if (detail != null) {
                            name = detail.getName();
                        }
                    } catch (Exception ignore) {}
                }
            }
            Map<String, Object> res = new HashMap<>();
            // 只返回名字和分数
            res.put("name", name != null ? name : userId);
            res.put("score", score);
            if (recognized) {
                result.put("code", 0);
                result.put("msg", "识别成功");
                result.put("result", res);
            } else {
                result.put("code", 1);
                result.put("msg", "未识别到人脸或未注册用户");
            }
            // 数据库写入 access_log（无论识别是否成功都写）
            String mac = MacUtil.getLocalMac();
            AccessDevice dev = accessDeviceMapper.selectById(mac);
            if (dev == null) {
                dev = new AccessDevice();
                dev.setDeviceId(mac);
                dev.setDeviceName("自动注册设备-" + mac);
                dev.setDeviceType(1);
                dev.setLocation("自动注册");
                dev.setStatus(1);
                dev.setRemark("系统自动注册");
                accessDeviceMapper.insert(dev);
            }
            // 保证每次插入前deviceId有值
            if (dev != null && dev.getDeviceId() == null) {
                dev.setDeviceId(mac);
            }
            AccessLog log = new AccessLog();
            if (recognized && userId != null) {
                try {
                    Person p = new Person();
                    p.setId(Long.parseLong(userId));
                    log.setPerson(p);
                } catch (Exception ignore) {}
            } else {
                log.setPerson(null); // 未识别到人脸
            }
            log.setAccessTime(LocalDateTime.now());
            log.setAccessType(1); // 1-人脸
            log.setResult(recognized ? ((score != null && score >= 80) ? 1 : 0) : 0); // 未识别到人脸 result=0
            log.setConfidence(score != null ? score.floatValue() : null);
            log.setCaptureImage(saveFile.getAbsolutePath());
            log.setTemperature(null); // 暂无体温
            log.setDevice(dev);
            accessLogMapper.insert(log);
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "异常: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    // 新增：查询某个人的人脸数据
    @GetMapping("/face_data")
    public ResponseEntity<?> getFaceDataByPersonId(@RequestParam("personId") Long personId) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (personId == null) {
                result.put("code", 1);
                result.put("msg", "personId不能为空");
                return ResponseEntity.ok(result);
            }
            java.util.List<FaceData> list = faceDataMapper.selectByPersonId(personId);
            java.util.List<Map<String, Object>> data = new java.util.ArrayList<>();
            for (FaceData fd : list) {
                Map<String, Object> item = new HashMap<>();
                item.put("personId", personId);
                item.put("featureData", fd.getFeatureData() != null ? java.util.Base64.getEncoder().encodeToString(fd.getFeatureData()) : null);
                item.put("imagePath", fd.getImagePath());
                item.put("registerTime", fd.getRegisterTime());
                item.put("qualityScore", fd.getQualityScore());
                item.put("version", fd.getVersion());
                data.add(item);
            }
            result.put("code", 0);
            result.put("msg", "查询成功");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "异常: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    // 新增：删除某个人的人脸数据
    @DeleteMapping("/face_data/{id}")
    public ResponseEntity<?> deleteFaceData(@PathVariable("id") Long personId) {
        Map<String, Object> result = new HashMap<>();
        try {
            int rows = faceDataMapper.deleteByPersonId(personId);
            result.put("code", 0);
            result.put("msg", "删除成功");
            result.put("rows", rows);
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "异常: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    // 新增：查询所有人脸数据（可分页）
    @GetMapping("/face_data/list")
    public ResponseEntity<?> listFaceData(@RequestParam(value = "page", defaultValue = "1") int page,
                                          @RequestParam(value = "size", defaultValue = "20") int size) {
        Map<String, Object> result = new HashMap<>();
        try {
            int offset = (page - 1) * size;
            java.util.List<FaceData> list = faceDataMapper.selectPaged(offset, size);
            result.put("code", 0);
            result.put("msg", "查询成功");
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "异常: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }
} 