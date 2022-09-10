package com.bys.reggie.common;

/**
 * @author Administrator
 * @version 1.0
 * @description: 自定义业务异常
 * @date 2022/9/9 13:04
 */
public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }
}
