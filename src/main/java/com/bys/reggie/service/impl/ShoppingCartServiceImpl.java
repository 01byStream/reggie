package com.bys.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bys.reggie.entity.ShoppingCart;
import com.bys.reggie.service.ShoppingCartService;
import com.bys.reggie.mapper.ShoppingCartMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【shopping_cart(购物车)】的数据库操作Service实现
* @createDate 2022-09-16 15:35:08
*/
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
    implements ShoppingCartService{

}




