package com.example.demo.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * MAC地址相关工具类
 * 提供获取本机MAC地址等功能
 */
public class MacUtil {
    public static String getLocalMac() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(ip);
            byte[] mac = ni.getHardwareAddress();
            if (mac == null) return null;
            StringBuilder sb = new StringBuilder();
            for (byte b : mac) {
                sb.append(String.format("%02X-", b));
            }
            if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
} 