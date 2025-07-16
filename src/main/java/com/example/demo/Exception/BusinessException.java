package com.example.demo.Exception;

/**
 * 自定义业务异常类
 * 用于抛出业务相关的错误
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
