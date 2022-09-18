package com.bys.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bys.reggie.common.R;
import com.bys.reggie.dto.DishDto;
import com.bys.reggie.entity.*;
import com.bys.reggie.service.*;
import com.bys.reggie.mapper.DishMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    @Resource
    CategoryService categoryService;

    @Resource
    SetmealDishService setmealDishService;

    @Resource
    SetmealService setmealService;

    /**
     * @description: 菜品信息分页查询，包括分类名称
     * @param page 分页第几页
     * @param pageSize 每页记录数量
     * @param name 菜品名称
     * @return R<Page>
     * @author Administrator
     * @date 2022/9/11 11:02
     */
    @Transactional
    @Override
    public R<Page> pageInfo(int page, int pageSize, String name) {
        //创建分页对象
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dtoPage = new Page<>();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //查询菜品名称
        queryWrapper.like(StringUtils.isNotBlank(name), Dish::getName, name);
        //排序
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        this.page(dishPage, queryWrapper); //得到菜品分页信息
        //拷贝分页数据
        BeanUtils.copyProperties(dishPage, dtoPage, "records");
        //取出records
        List<Dish> records = dishPage.getRecords();
        //转换成DishDto
        List<DishDto> dishDtoList = records.stream().map(item -> {
            DishDto dishDto = new DishDto();
            //拷贝属性
            BeanUtils.copyProperties(item, dishDto);
            Category category = categoryService.getById(item.getCategoryId());
            dishDto.setCategoryName(category.getName());
            return dishDto;
        }).collect(Collectors.toList());
        //分页数据放入page
        dtoPage.setRecords(dishDtoList);
        return R.success(dtoPage);
    }

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

    /**
     * @description: 更新菜品信息以及携带的口味信息
     * @param dishDto 携带口味信息的菜品对象
     * @author Administrator
     * @date 2022/9/10 22:25
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品表
        this.updateById(dishDto);
        //更新口味表，先删除所有的口味信息，再添加新的口味信息
        LambdaQueryWrapper<DishFlavor> dishFlavorWrapper = new LambdaQueryWrapper<>();
        dishFlavorWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(dishFlavorWrapper); //删除口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        //添加菜品id
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(flavors); //插入新的口味信息
    }

    /**
     * @description: 删除菜品，同时删除菜品携带的口味信息
     * @param ids 菜品id列表
     * @author Administrator
     * @date 2022/9/11 10:35
     */
    @Transactional
    @Override
    public void removeWithFlavor(List<Long> ids) {
        //删除菜品表的记录
        this.removeByIds(ids);
        //删除口味表的记录，停售相应的套餐
        for (Long id : ids) {
            //删除口味表记录
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, id);
            dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
            //停售相应的套餐
            LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            setmealDishLambdaQueryWrapper.eq(SetmealDish::getDishId, id);
            List<SetmealDish> setmealDishes = setmealDishService.list(setmealDishLambdaQueryWrapper);
            List<Setmeal> setmeals = setmealDishes.stream().map(setmealDish -> {
                Setmeal setmeal = new Setmeal();
                setmeal.setStatus(0);
                setmeal.setId(setmealDish.getSetmealId());
                return setmeal;
            }).collect(Collectors.toList());
            setmealService.updateBatchById(setmeals);
        }
    }

    /**
     * @description: 根据id查询菜品信息以及携带的口味信息和分类名称
     * @param id 菜品id
     * @return R<DishDto>
     * @author Administrator
     * @date 2022/9/11 10:57
     */
    @Transactional
    @Override
    public R<DishDto> getByIdWithFlavor(Long id) {
        //获取菜品信息
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        //查询分类名称
        Category category = categoryService.getById(dish.getCategoryId());
        dishDto.setCategoryName(category.getName());
        //查询口味信息
        LambdaQueryWrapper<DishFlavor> flavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        flavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavorList = dishFlavorService.list(flavorLambdaQueryWrapper);
        dishDto.setFlavors(flavorList);

        return R.success(dishDto);
    }

    /**
     * @description: 更新菜品状态，包括菜品关联的套餐状态
     * @param status 状态
     * @param ids 菜品id列表
     * @author Administrator
     * @date 2022/9/12 21:42
     */
    @Transactional
    @Override
    public void updateStatus(int status, List<Long> ids) {
        //更新菜品状态
        List<Dish> dishList = ids.stream().map(id -> {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setStatus(status);
            return dish;
        }).collect(Collectors.toList());
        this.updateBatchById(dishList);
        //更新关联的套餐状态
        for (Long id : ids) {
            //查询套餐菜品关系表
            LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            setmealDishLambdaQueryWrapper.eq(SetmealDish::getDishId, id);
            List<SetmealDish> setmealDishes = setmealDishService.list(setmealDishLambdaQueryWrapper);
            //查找菜品关联套餐
            List<Setmeal> setmeals = setmealDishes.stream().map(setmealDish -> {
                Setmeal setmeal = new Setmeal();
                setmeal.setStatus(0);
                setmeal.setId(setmealDish.getSetmealId());
                return setmeal;
            }).collect(Collectors.toList());
            //更新套餐状态
            setmealService.updateBatchById(setmeals);
        }
    }

    /**
     * @description: 获取菜品信息列表，包括口味信息
     * @param dish 菜品对象
     * @return List<DishDto>
     * @author Administrator
     * @date 2022/9/16 14:40
     */
    @Transactional
    @Override
    public List<DishDto> getList(Dish dish) {
        List<DishDto> dishDtoList = null;
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //查询分类id
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //查询起售状态的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        //排序
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = this.list(queryWrapper); //得到dish列表
        if (dishList == null) { //没有找到菜品，直接结束，返回空列表
            return dishDtoList;
        }
        //复制到dto对象列表
        dishDtoList = dishList.stream().map(dishItem -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dishItem, dishDto);
            //查询口味信息
            LambdaQueryWrapper<DishFlavor> flavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            flavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
            List<DishFlavor> flavorList = dishFlavorService.list(flavorLambdaQueryWrapper);
            dishDto.setFlavors(flavorList);
            //查询分类名称
            Category category = categoryService.getById(dishDto.getCategoryId());
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }

            return dishDto;
        }).collect(Collectors.toList());

        return dishDtoList;
    }
}




