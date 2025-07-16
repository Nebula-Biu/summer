package com.example.demo.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * 雪花算法ID生成工具类
 * 用于生成全局唯一ID
 */
public class SnowFlake {

    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(1, 1);

    public static long nextId() {
        return SNOWFLAKE.nextId();
    }

    public static String nextIdStr(){
        return SNOWFLAKE.nextIdStr();
    }

}
