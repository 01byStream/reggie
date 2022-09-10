package com.bys.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bys.reggie.entity.DishFlavor;
import com.bys.reggie.service.DishFlavorService;
import com.bys.reggie.mapper.DishFlavorMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【dish_flavor(菜品口味关系表)】的数据库操作Service实现
* @createDate 2022-09-10 15:52:15
*/
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>
    implements DishFlavorService{

}




