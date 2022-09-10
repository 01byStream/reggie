package com.bys.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bys.reggie.common.CustomException;
import com.bys.reggie.entity.Category;
import com.bys.reggie.entity.Dish;
import com.bys.reggie.entity.Setmeal;
import com.bys.reggie.service.CategoryService;
import com.bys.reggie.mapper.CategoryMapper;
import com.bys.reggie.service.DishService;
import com.bys.reggie.service.SetmealService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author Administrator
* @description 针对表【category(菜品及套餐分类)】的数据库操作Service实现
* @createDate 2022-09-10 15:52:15
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

    @Resource
    private DishService dishService;
    @Resource
    private SetmealService setmealService;

    /**
     * @description: 根据分类id删除分类，删除之前需要判断是否关联菜品或套餐
     * @param id 分类id
     * @author Administrator
     * @date 2022/9/9 11:39
     */
    @Override
    public void removeAllById(Long id) {
        //查询当前分类是否关联了菜品或套餐，如果已关联，抛出一个业务异常
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishWrapper);
        if (count1 > 0) {
            //抛出业务异常
            throw new CustomException("当前分类关联了菜品，不能删除！");
        }
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setmealWrapper);
        if (count2 > 0) {
            //抛出业务异常
            throw new CustomException("当前分类关联了套餐，不能删除！");
        }
        //未关联，直接删除
        this.removeById(id);
    }

}




