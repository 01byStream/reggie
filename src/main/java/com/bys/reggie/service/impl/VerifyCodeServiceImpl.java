package com.bys.reggie.service.impl;

import com.bys.reggie.service.VerifyCodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 * @version 1.0
 * @description: 验证码服务实现
 * @date 2022/9/13 10:44
 */
@Service
public class VerifyCodeServiceImpl implements VerifyCodeService {
    @Resource
    JavaMailSender sender;

    @Resource
    StringRedisTemplate template;

    @Value("${spring.mail.username}")
    String fromMail; //发送者邮箱地址

    //发送验证码
    @Override
    public void sendVerifyCode(String mail) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        //设置标题
        mailMessage.setSubject("【瑞吉外卖】验证码");
        //生成随机验证码
        Random random = new Random();
        int verifyCode = random.nextInt(899999) + 100000;
        //存储验证码，三分钟有效
        template.opsForValue().set("verify:code:"+ mail, verifyCode + "", 3, TimeUnit.MINUTES);
        //编辑短信内容
        mailMessage.setText("您的验证码为：" + verifyCode + "，三分钟内有效，请及时使用！如果不是本人操作，请勿将验证码告诉任何人！");
        //设置接收者
        mailMessage.setTo(mail);
        //发送者
        mailMessage.setFrom(fromMail);
        //发送
        sender.send(mailMessage);
    }

    //判断验证码是否正确
    @Override
    public boolean confirmCode(String mail, String code) {
        String verifyCode = template.opsForValue().get("verify:code:" + mail);
        if (code == null) {
            return false;
        }
        if (code.equals(verifyCode)) {
            template.delete("verify:code:" + mail);
            return true;
        }
        return false;
    }
}
