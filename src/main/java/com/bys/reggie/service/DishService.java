package com.bys.reggie.service;

import com.bys.reggie.dto.DishDto;
import com.bys.reggie.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【dish(菜品管理)】的数据库操作Service
* @createDate 2022-09-10 15:52:15
*/
public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);
}
