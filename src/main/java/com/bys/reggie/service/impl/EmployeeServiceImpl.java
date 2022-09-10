package com.bys.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bys.reggie.common.R;
import com.bys.reggie.entity.Employee;
import com.bys.reggie.service.EmployeeService;
import com.bys.reggie.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Administrator
 * @description 针对表【employee(员工信息)】的数据库操作Service实现
 * @createDate 2022-09-10 15:52:15
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>
        implements EmployeeService {

    /**
     * @return R
     * @description: 验证登录信息，返回验证结果
     * @author Administrator
     * @date 2022/9/5 11:06
     */
    @Override
    public R<Employee> authEmployee(Employee employee, HttpServletRequest request) {
        //1. 将页面提交的password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2. 根据页面提交的username查询数据库，没有则返回登录失败结果
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = this.getOne(queryWrapper);
        //判断是否该用户
        if (emp == null) {
            return R.error("登录失败，用户名不存在");
        }
        //3. 进行密码对比，不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败，密码错误");
        }
        //4. 查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("登录失败，账号已经禁用");
        }
        //5. 登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());

        return R.success(emp);
    }

    /**
     * @param employee 前端输入的部分员工信息
     * @return R<String>
     * @description: 向数据库插入一条完整的员工信息
     * @author Administrator
     * @date 2022/9/6 11:29
     */
    @Override
    public R<String> addEmployee(HttpServletRequest request, Employee employee) {
        //设置初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        this.save(employee); //保存到数据库
        return R.success("添加成功");
    }

    /**
     * @param page     第几页
     * @param pageSize 每页记录数
     * @param name     查找指定账号
     * @return R<Page>
     * @description: 分页查询员工信息
     * @author Administrator
     * @date 2022/9/6 19:18
     */
    @Override
    public R<Page> getPage(int page, int pageSize, String name) {
        //1. 构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        //2. 构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //3. 添加过滤条件
        queryWrapper.like(StringUtils.isNotBlank(name), Employee::getUsername, name);
        //4. 添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //5. 执行查询
        this.page(pageInfo, queryWrapper);
        //6. 返回结果
        return R.success(pageInfo);
    }
}




