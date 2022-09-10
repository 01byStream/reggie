package com.bys.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bys.reggie.dto.DishDto;
import com.bys.reggie.entity.Dish;
import com.bys.reggie.entity.DishFlavor;
import com.bys.reggie.service.DishFlavorService;
import com.bys.reggie.service.DishService;
import com.bys.reggie.mapper.DishMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【dish(菜品管理)】的数据库操作Service实现
* @createDate 2022-09-10 15:52:15
*/
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
    implements DishService{

    @Resource
    DishFlavorService dishFlavorService;

    /**
     * @description: 向数据库保存菜品信息以及携带的口味信息
     * @param dishDto 携带口味信息的菜品对象
     * @author Administrator
     * @date 2022/9/10 15:18
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //插入菜品信息
        this.save(dishDto);
        //获取口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        //前端传入的口味信息列表里面没有菜品id，需要手动设置
        flavors = flavors.stream().map(item -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        //插入口味信息
        dishFlavorService.saveBatch(flavors);
    }
}




