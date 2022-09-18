package com.bys.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bys.reggie.dto.OrdersDto;
import com.bys.reggie.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【orders(订单表)】的数据库操作Service
* @createDate 2022-09-17 09:32:14
*/
public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);

    void againSubmit(Orders orders);

    Page<OrdersDto> getUserPage(int page, int pageSize);

    Page<OrdersDto> getPage(int page, int pageSize, String number,String beginTime,String endTime);
}
