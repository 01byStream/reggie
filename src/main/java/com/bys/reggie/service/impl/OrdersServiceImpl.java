package com.bys.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bys.reggie.common.BaseContext;
import com.bys.reggie.common.CustomException;
import com.bys.reggie.dto.OrdersDto;
import com.bys.reggie.entity.*;
import com.bys.reggie.service.*;
import com.bys.reggie.mapper.OrdersMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【orders(订单表)】的数据库操作Service实现
* @createDate 2022-09-17 09:32:14
*/
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
    implements OrdersService{

    @Resource
    private SetmealService setmealService;

    @Resource
    private DishService dishService;

    @Resource
    private ShoppingCartService shoppingCartService;

    @Resource
    private AddressBookService addressBookService;

    @Resource
    private UserService userService;

    @Resource
    private OrderDetailService orderDetailService;

    /**
     * @description: 提交订单
     * @param orders 订单信息
     * @author Administrator
     * @date 2022/9/17 10:30
     */
    @Transactional
    @Override
    public void submit(Orders orders) {
        Long userId = BaseContext.getCurrentId();
        //查询用户数据
        User user = userService.getById(userId);
        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> cartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cartLambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(cartLambdaQueryWrapper);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new CustomException("购物车为空，无法下单");
        }
        Long orderId = IdWorker.getId(); //生成订单号
        //原子操作，保证线程安全
        AtomicInteger amount = new AtomicInteger(0);
        //遍历购物车的记录
        List<OrderDetail> orderDetails = shoppingCartList.stream().map(cartOne -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setName(cartOne.getName());
            orderDetail.setImage(cartOne.getImage());
            orderDetail.setOrderId(orderId);
            orderDetail.setDishId(cartOne.getDishId());
            orderDetail.setSetmealId(cartOne.getSetmealId());
            orderDetail.setDishFlavor(cartOne.getDishFlavor());
            orderDetail.setNumber(cartOne.getNumber());
            orderDetail.setAmount(cartOne.getAmount());
            //计算订单总金额, addAndGet --> "+="
            amount.addAndGet(cartOne.getAmount().multiply(new BigDecimal(cartOne.getNumber())).intValue());
            //插入订单详细数据
            return orderDetail;
        }).collect(Collectors.toList());
        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null) {
            throw new CustomException("地址为空，无法下单");
        }

        //向订单表插入一条数据
        orders.setId(orderId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        orders.setUserName(user.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        this.save(orders);//向订单详情表插入数据
        orderDetailService.saveBatch(orderDetails);
        //清空购物车数据
        shoppingCartService.remove(cartLambdaQueryWrapper);
    }

    //用户订单分页查询
    @Transactional
    @Override
    public Page<OrdersDto> getUserPage(int page, int pageSize) {
        Page<OrdersDto> ordersDtoPage = new Page<>();
        //查询当前用户
        User user = userService.getById(BaseContext.getCurrentId());
        //查询基础订单列表
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(Orders::getUserId, user.getId());
        //排序
        ordersLambdaQueryWrapper.orderByDesc(Orders::getCheckoutTime);
        //得到基本订单列表
        this.page(ordersPage, ordersLambdaQueryWrapper);
        //赋值给dto列表
        BeanUtils.copyProperties(ordersPage, ordersDtoPage, "records");
        List<Orders> ordersList = ordersPage.getRecords();
        //添加订单详细信息
        List<OrdersDto> ordersDtoList = ordersList.stream().map(orders -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(orders, ordersDto);
            //查询订单详情列表
            LambdaQueryWrapper<OrderDetail> detailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            detailLambdaQueryWrapper.eq(OrderDetail::getOrderId, orders.getId());
            List<OrderDetail> orderDetailList = orderDetailService.list(detailLambdaQueryWrapper);
            ordersDto.setOrderDetails(orderDetailList);

            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(ordersDtoList);

        return ordersDtoPage;
    }

    //订单分页查询
    @Override
    public Page<OrdersDto> getPage(int page, int pageSize, String number, String beginTime, String endTime) {
        Page<OrdersDto> ordersDtoPage = new Page<>();
        //查询基础订单列表
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //条件查询
        ordersLambdaQueryWrapper.like(number != null, Orders::getNumber, number);
        ordersLambdaQueryWrapper.ge(beginTime != null, Orders::getCheckoutTime, beginTime);
        ordersLambdaQueryWrapper.le(endTime != null, Orders::getCheckoutTime, endTime);
        //排序
        ordersLambdaQueryWrapper.orderByDesc(Orders::getCheckoutTime);
        //得到基本订单列表
        this.page(ordersPage, ordersLambdaQueryWrapper);
        //赋值给dto列表
        BeanUtils.copyProperties(ordersPage, ordersDtoPage, "records");
        List<Orders> ordersList = ordersPage.getRecords();
        //添加订单详细信息
        List<OrdersDto> ordersDtoList = ordersList.stream().map(orders -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(orders, ordersDto);
            //查询订单详情列表
            LambdaQueryWrapper<OrderDetail> detailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            detailLambdaQueryWrapper.eq(OrderDetail::getOrderId, orders.getId());
            List<OrderDetail> orderDetailList = orderDetailService.list(detailLambdaQueryWrapper);
            ordersDto.setOrderDetails(orderDetailList);

            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(ordersDtoList);

        return ordersDtoPage;
    }

    //再来一单
    @Transactional
    @Override
    public void againSubmit(Orders orders) {
        //查询历史订单详情
        LambdaQueryWrapper<OrderDetail> detailLambdaQueryWrapper = new LambdaQueryWrapper<>();
        detailLambdaQueryWrapper.eq(OrderDetail::getOrderId, orders.getId());
        List<OrderDetail> orderDetailList = orderDetailService.list(detailLambdaQueryWrapper);
        //再次下单，购物车添加菜品
        //移除已停售菜品
        for (OrderDetail orderDetail : orderDetailList) {
            //套餐或菜品 --> 停售/不存在 --> 删除记录
            Long dishId = orderDetail.getDishId();
            if (dishId != null) {
                Dish dish = dishService.getById(orderDetail.getDishId());
                if (dish == null || dish.getStatus() == 0) {
                    orderDetailList.remove(orderDetail);
                }
            } else {
                Setmeal setmeal = setmealService.getById(orderDetail.getSetmealId());
                if (setmeal == null || setmeal.getStatus() == 0) {
                    orderDetailList.remove(orderDetail);
                }
            }
        }
        //将菜品加入购物车
        List<ShoppingCart> cartList = orderDetailList.stream().map(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setName(orderDetail.getName());
            shoppingCart.setImage(orderDetail.getImage());
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setDishId(orderDetail.getDishId());
            shoppingCart.setSetmealId(orderDetail.getSetmealId());
            shoppingCart.setDishFlavor(orderDetail.getDishFlavor());
            shoppingCart.setNumber(orderDetail.getNumber());
            shoppingCart.setAmount(orderDetail.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        shoppingCartService.saveBatch(cartList);
    }
}




