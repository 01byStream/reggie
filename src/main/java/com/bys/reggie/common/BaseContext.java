package com.bys.reggie.common;

/**
 * @author Administrator
 * @version 1.0
 * @description: 基于ThreadLocal封装的工具类，用于保存和获取当前登录用户的id
 * @date 2022/9/7 22:15
 */
public class BaseContext {
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    //保存id
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    //获取id
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
