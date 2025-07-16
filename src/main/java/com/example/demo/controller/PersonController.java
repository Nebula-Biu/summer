package com.example.demo.controller;


import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.mapper.PersonMapper;
import com.example.demo.mapper.PersonDetailMapper;
import com.example.demo.mapper.FaceDataMapper;
import com.example.demo.entity.Person;
import com.example.demo.entity.PersonDetail;
import com.example.demo.entity.FaceData;
import com.example.demo.utils.BaiduAuth;
import com.example.demo.utils.BaiduFaceRegister;
import com.example.demo.utils.SnowFlake;
import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * 人员相关接口控制器
 * 包含注册、查询、查重等API
 */
@RestController
@RequestMapping("/api")
public class PersonController {
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private PersonDetailMapper personDetailMapper;
    @Autowired
    private FaceDataMapper faceDataMapper;

    /**
     * 注册人员接口
     */
    @PostMapping("/person")
    public Map<String, Object> register(@RequestBody Map<String, String> payload) {
        Map<String, Object> result = new HashMap<>();
        try {
            String name = payload.get("name");
            String genderStr = payload.get("gender");
            String idCard = payload.get("id_card");
            String phone = payload.get("phone");
            String position = payload.get("position");
            String statusStr = payload.get("status");
            String base64Image = payload.get("image");
            if (name == null || name.isEmpty() || base64Image == null || base64Image.isEmpty()) {
                result.put("code", 1);
                result.put("msg", "参数缺失");
                return result;
            }
            Integer gender = null;
            Integer status = null;
            try { gender = genderStr != null ? Integer.parseInt(genderStr) : null; } catch (Exception ignore) {}
            try { status = statusStr != null ? Integer.parseInt(statusStr) : null; } catch (Exception ignore) {}
            String accessToken = BaiduAuth.getAccessToken();
            Long personId = SnowFlake.nextId();
            // 保存图片到 photos/personId 目录
            File photosDir = new File("photos");
            if (!photosDir.exists()) photosDir.mkdir();
            File userDir = new File(photosDir, String.valueOf(personId));
            if (!userDir.exists()) userDir.mkdir();
            String fileName = personId + "_" + System.currentTimeMillis() + ".jpg";
            File saveFile = new File(userDir, fileName);
            String pureBase64 = base64Image.contains(",") ? base64Image.substring(base64Image.indexOf(",") + 1) : base64Image;
            byte[] imageBytes = java.util.Base64.getDecoder().decode(pureBase64);
            java.nio.file.Files.write(saveFile.toPath(), imageBytes);
            // 注册到百度人脸库
            BaiduFaceRegister.registerFace(accessToken, "test_group", String.valueOf(personId), saveFile.getAbsolutePath());
            // person
            Person person = new Person();
            person.setId(personId);
            person.setName(name);
            personMapper.insertPerson(person);
            // person_detail
            PersonDetail detail = new PersonDetail();
            detail.setPersonId(personId);
            detail.setName(name);
            detail.setGender(gender);
            detail.setIdCard(idCard);
            detail.setPhone(phone);
            detail.setPosition(position);
            detail.setStatus(status != null ? status : 1);
            detail.setRegisterTime(LocalDateTime.now());
            detail.setUpdateTime(LocalDateTime.now());
            personDetailMapper.insert(detail);
            // face_data
            FaceData faceData = new FaceData();
            faceData.setPerson(person);
            faceData.setImagePath(saveFile.getAbsolutePath());
            faceData.setRegisterTime(LocalDateTime.now());
            faceData.setQualityScore(null);
            faceData.setVersion("1.0.0");
            faceData.setFeatureData(new byte[1]); // 占位
            faceDataMapper.insert(faceData);
            Map<String, Object> data = new HashMap<>();
            data.put("personId", personId);
            data.put("name", name);
            result.put("code", 0);
            result.put("msg", "注册成功");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "异常: " + e.getMessage());
        }
        return result;
    }

    /**
     * 查询人员详细信息接口
     */
    @GetMapping("/person/{id}")
    public Map<String, Object> getPersonDetail(@PathVariable("id") Long id) {
        Map<String, Object> result = new HashMap<>();
        PersonDetail detail = personDetailMapper.selectByPersonId(id);
        if (detail != null) {
            result.put("code", 0);
            result.put("msg", "查询成功");
            result.put("data", detail);
        } else {
            result.put("code", 1);
            result.put("msg", "未找到该人员");
            result.put("data", null);
        }
        return result;
    }

    /**
     * 分页查询人员列表接口
     */
    @GetMapping("/list")
    public Map<String, Object> listPersons(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        Map<String, Object> result = new HashMap<>();
        try {
            int offset = (page - 1) * size;
            // 直接用SQL分页查询
            List<PersonDetail> list = personDetailMapper.selectPaged(offset, size);
            result.put("code", 0);
            result.put("msg", "查询成功");
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "异常: " + e.getMessage());
        }
        return result;
    }

    /**
     * 检查姓名是否存在接口
     */
    @GetMapping("/person/checkName")
    public Map<String, Object> checkName(@RequestParam("name") String name) {
        Map<String, Object> result = new HashMap<>();
        boolean exists = false;
        if (name != null && !name.trim().isEmpty()) {
            exists = personDetailMapper.selectByName(name.trim()) != null;
        }
        result.put("exists", exists);
        return result;
    }

    /**
     * 检查身份证号是否存在接口
     */
    @GetMapping("/person/checkIdCard")
    public Map<String, Object> checkIdCard(@RequestParam("id_card") String idCard) {
        Map<String, Object> result = new HashMap<>();
        boolean exists = false;
        if (idCard != null && !idCard.trim().isEmpty()) {
            exists = personDetailMapper.selectByIdCard(idCard.trim()) != null;
        }
        result.put("exists", exists);
        return result;
    }

    // 删除接口已移除
}