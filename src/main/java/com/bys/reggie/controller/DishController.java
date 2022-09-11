package com.bys.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bys.reggie.common.R;
import com.bys.reggie.dto.DishDto;
import com.bys.reggie.entity.Dish;
import com.bys.reggie.service.DishService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @version 1.0
 * @description: 菜品管理
 * @date 2022/9/10 11:36
 */
@RestController
@RequestMapping("/dish")
public class DishController {
    @Resource
    private DishService dishService;

    //新增菜品，保存菜品信息以及口味信息
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("菜品添加成功");
    }

    //修改菜品信息
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    //根据id删除菜品、批量删除
    @DeleteMapping
    public R<String> remove(@RequestParam List<Long> ids) {
        dishService.removeWithFlavor(ids);
        return R.success("删除成功");
    }

    //分页查询
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        return dishService.pageInfo(page, pageSize, name);
    }

    //修改页面回显菜品信息
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        return dishService.getByIdWithFlavor(id);
    }

    //起售停售、批量起售、停售
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable int status, @RequestParam List<Long> ids) {
        List<Dish> dishList = ids.stream().map(id -> {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setStatus(status);
            return dish;
        }).collect(Collectors.toList());
        dishService.updateBatchById(dishList);
        return R.success("状态修改成功");
    }

    //根据分类id查询菜品信息列表
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //查询分类id
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //查询起售状态的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        //排序
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(queryWrapper);
        return R.success(dishList);
    }

}
