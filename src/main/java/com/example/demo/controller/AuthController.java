package com.example.demo.controller;

import com.example.demo.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证相关接口控制器
 * 提供登录等API
 */
@RestController
@RequestMapping("/api")
public class AuthController {
    // 简单演示：用户名密码写死，实际应查数据库
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");
        Map<String, Object> result = new HashMap<>();
        if ("admin".equals(username) && "123456".equals(password)) {
            String token = JwtUtil.generateToken(username);
            result.put("code", 0);
            result.put("token", token);
        } else {
            result.put("code", 1);
            result.put("msg", "用户名或密码错误");
        }
        return result;
    }
} 