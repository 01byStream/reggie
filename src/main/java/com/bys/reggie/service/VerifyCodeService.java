package com.bys.reggie.service;

/**
 * @author Administrator
 * @version 1.0
 * @description: 验证码服务接口
 * @date 2022/9/13 10:44
 */
public interface VerifyCodeService {

    void sendVerifyCode(String mail);

    boolean confirmCode(String mail, String code);

}
