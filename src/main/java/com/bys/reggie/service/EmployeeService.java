package com.bys.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bys.reggie.common.R;
import com.bys.reggie.entity.Employee;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author Administrator
* @description 针对表【employee(员工信息)】的数据库操作Service
* @createDate 2022-09-10 15:52:15
*/
public interface EmployeeService extends IService<Employee> {
    R<Employee> authEmployee(Employee employee, HttpServletRequest request);

    R<String> addEmployee(HttpServletRequest request, Employee employee);

    R<Page> getPage(int page, int pageSize, String name);


}
