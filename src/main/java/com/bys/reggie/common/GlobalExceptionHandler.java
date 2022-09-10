package com.bys.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author Administrator
 * @version 1.0
 * @description: 全局异常处理
 * @date 2022/9/6 12:26
 */
@Slf4j
@ResponseBody
@ControllerAdvice(annotations = {RestController.class, Controller.class}) //指定拦截异常处理的类
public class GlobalExceptionHandler {

    /**
     * @description: 数据库完整性约束--异常处理
     * @param e 捕获的异常
     * @return R<String>
     * @author Administrator
     * @date 2022/9/6 12:35
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException e) {
        String message = e.getMessage();
        log.error(message);
        //字段重复
        if (message.contains("Duplicate entry")) {
            String[] split = message.split(" ");
            String errMsg = split[2] + "已存在";
            return R.error(errMsg);
        }

        //其他异常
        return R.error("未知错误");
    }

    //捕获自定义的业务异常
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException e) {
        return R.error(e.getMessage());
    }
}
