package com.bys.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bys.reggie.common.BaseContext;
import com.bys.reggie.common.R;
import com.bys.reggie.entity.ShoppingCart;
import com.bys.reggie.service.ShoppingCartService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description: 购物车对应的控制器
 * @date 2022/9/16 15:37
 */
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Resource
    private ShoppingCartService shoppingCartService;

    @GetMapping("list")
    public R<List<ShoppingCart>> list() {
        //查询用户购物车
        LambdaQueryWrapper<ShoppingCart> cartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cartLambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        //排序
        cartLambdaQueryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        //得到结果返回
        List<ShoppingCart> cartList = shoppingCartService.list(cartLambdaQueryWrapper);
        return R.success(cartList);
    }

    @PostMapping("/add")
    public R<ShoppingCart> addOne(@RequestBody ShoppingCart shoppingCart) {
        //查找当前用户的购物车记录
        shoppingCart.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<ShoppingCart> cartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cartLambdaQueryWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());
        //判断当前加入购物车的是菜品还是套餐，查找是否已添加
        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
            //加入的是菜品，
            cartLambdaQueryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            cartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        //查找当前购物车里的菜品或套餐记录
        ShoppingCart cartOne = shoppingCartService.getOne(cartLambdaQueryWrapper);
        if (cartOne == null) {
            //第一次添加
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            cartOne = shoppingCart;
            shoppingCartService.save(cartOne);
        } else {
            Integer number = cartOne.getNumber();
            cartOne.setNumber(number + 1);
            //更新
            shoppingCartService.updateById(cartOne);
        }

        return R.success(cartOne);
    }

    @PostMapping("/sub")
    public R<ShoppingCart> subOne(@RequestBody ShoppingCart shoppingCart) {
        //查找当前用户的购物车记录
        shoppingCart.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<ShoppingCart> cartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cartLambdaQueryWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());
        //判断当前减少的是菜品还是套餐
        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
            //菜品
            cartLambdaQueryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            cartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        //查找当前购物车里的菜品或套餐记录
        ShoppingCart cartOne = shoppingCartService.getOne(cartLambdaQueryWrapper);
        if (cartOne.getNumber() == 1) {
            //删除
            shoppingCartService.removeById(cartOne);
            cartOne.setNumber(0);
        } else {
            Integer number = cartOne.getNumber();
            cartOne.setNumber(number - 1);
            //更新
            shoppingCartService.updateById(cartOne);
        }

        return R.success(cartOne);
    }

    @DeleteMapping("/clean")
    public R<String> clean() {
        LambdaQueryWrapper<ShoppingCart> cartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cartLambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(cartLambdaQueryWrapper);
        return R.success("删除成功");
    }
}
