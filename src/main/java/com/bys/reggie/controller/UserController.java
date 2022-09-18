package com.bys.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bys.reggie.common.R;
import com.bys.reggie.entity.User;
import com.bys.reggie.service.UserService;
import com.bys.reggie.service.VerifyCodeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 * @description: 前端用户控制器
 * @date 2022/9/13 15:42
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService userService;

    @Resource
    VerifyCodeService verifyCodeService;

    @PostMapping("/sendMsg")
    public R<String> sendCode(@RequestBody User user) {
        if (user.getPhone() == null) {
            return R.error("验证码发送失败");
        }
        //发送验证码
        verifyCodeService.sendVerifyCode(user.getPhone());
        return R.success("验证码发送成功");
    }

    @PostMapping("/login")
    public R<User> login(HttpServletRequest request, @RequestBody Map<String, String> map) {
        String mail = map.get("phone");
        String code = map.get("code");
        //判断验证码是否正确
        boolean isLogin = verifyCodeService.confirmCode(mail, code);
        if (!isLogin) {
            return R.error("登录失败");

        }
        //查询该邮箱用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, mail);
        User user = userService.getOne(queryWrapper);
        if (user == null) { //没有找到，第一次登录，插入用户数据
            user = new User(); //新建一个User对象用于保存用户数据，完成注册
            user.setPhone(mail);
            userService.save(user);
        }
        HttpSession session = request.getSession();
        session.setAttribute("user", user.getId()); //保存用户
        return R.success(user);
    }

    //用户登出
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("user");
        return R.success("退出登录成功");
    }
}
