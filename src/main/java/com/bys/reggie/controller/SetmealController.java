package com.bys.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bys.reggie.common.R;
import com.bys.reggie.dto.SetmealDto;
import com.bys.reggie.entity.Dish;
import com.bys.reggie.entity.Setmeal;
import com.bys.reggie.service.SetmealService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @version 1.0
 * @description: 套餐管理控制器
 * @date 2022/9/11 17:48
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Resource
    SetmealService setmealService;

    //新增套餐
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("添加套餐成功");
    }

    //套餐分页显示
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<SetmealDto> pageInfo = setmealService.pageInfo(page, pageSize, name);
        return R.success(pageInfo);
    }

    //修改套餐页面信息回显
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    //修改套餐信息
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithDish(setmealDto);
        return R.success("修改成功");
    }

    //删除、批量删除
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        setmealService.removeWithDish(ids);
        return R.success("删除套餐成功");
    }

    //停售、起售、批量停售、起售
    @CacheEvict(value = "setmealCache", allEntries = true)
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable int status, @RequestParam List<Long> ids) {
        List<Setmeal> setmeals = ids.stream().map(id -> {
            Setmeal setmeal = setmealService.getById(id);
            setmeal.setStatus(status);
            return setmeal;
        }).collect(Collectors.toList());
        setmealService.updateBatchById(setmeals); //批量更新状态
        return R.success("更新状态成功");
    }

    //获取菜品分类对应的套餐
    @GetMapping("list")
    public R<List<SetmealDto>> list(Setmeal setmeal) {
        List<SetmealDto> setmealDtoList = setmealService.getList(setmeal);
        return R.success(setmealDtoList);
    }

    //获取套餐下的菜品
    @GetMapping("/dish/{id}")
    public R<List<Dish>> getSetmealDish(@PathVariable Long id) {
        return null;
    }

}
