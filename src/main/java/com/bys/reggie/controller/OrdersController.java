package com.bys.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bys.reggie.common.R;
import com.bys.reggie.dto.OrdersDto;
import com.bys.reggie.entity.Orders;
import com.bys.reggie.service.OrdersService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Administrator
 * @version 1.0
 * @description: 订单表对应的控制器
 * @date 2022/9/17 9:33
 */
@RestController
@RequestMapping("/order")
public class OrdersController {
    @Resource
    private OrdersService ordersService;

    //提交订单
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    //分页查询用户订单
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> userPage(int page, int pageSize) {
        Page<OrdersDto> dtoPage = ordersService.getUserPage(page, pageSize);
        return R.success(dtoPage);
    }

    //管理后台订单分页查询
    @GetMapping("/page")
    public R<Page<OrdersDto>> page(int page, int pageSize, String number,
                                   String beginTime, String endTime) {
        Page<OrdersDto> dtoPage = ordersService.getPage(page, pageSize, number, beginTime, endTime);
        return R.success(dtoPage);
    }

    //再来一单
    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders) {
        ordersService.againSubmit(orders);
        return R.success("下单成功");
    }

    // 取消，派送，完成
    @PutMapping
    public R<Orders> update(@RequestBody Orders orders) {
        ordersService.updateById(orders);
        return R.success(orders);
    }

}
