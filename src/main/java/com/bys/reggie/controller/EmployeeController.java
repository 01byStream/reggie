package com.bys.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bys.reggie.common.R;
import com.bys.reggie.entity.Employee;
import com.bys.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Administrator
 * @version 1.0
 * @description: 员工相关的控制器
 * @date 2022/8/31 16:51
 */
@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {
    //注入相应的服务
    @Autowired
    private EmployeeService employeeService;

    /**
     * @description: 员工登录
     * @param request 请求，登录后将员工id放入session中
     * @param employee 获取前端返回的json数据
     * @return R<Employee> 返回登录结果
     * @author Administrator
     * @date 2022/8/31 19:19
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        return employeeService.authEmployee(employee, request);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //退出，移除Session信息
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * @description: 补全一条员工记录，向数据库插入
     * @param request 请求，用于获取创建者信息
     * @param employee 需要添加的员工信息
     * @return R<String>
     * @author Administrator
     * @date 2022/9/6 16:59
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        return employeeService.addEmployee(request, employee);
    }

    //员工信息分页查询
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        return employeeService.getPage(page, pageSize, name);
    }

    //更新员工信息
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        //执行更新
        employeeService.updateById(employee);
        return R.success("修改状态成功");
    }

    //根据id获取员工信息
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据id获取员工信息");
        Employee emp = employeeService.getById(id);
        if (emp == null) {
            return R.error("没有查询到该员工");
        }
        return R.success(emp);
    }

}
