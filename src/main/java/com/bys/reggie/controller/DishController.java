package com.bys.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bys.reggie.common.R;
import com.bys.reggie.dto.DishDto;
import com.bys.reggie.entity.Dish;
import com.bys.reggie.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "菜品相关接口")
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
    @ApiOperation(value = "菜品分页查询接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页菜品数", required = true),
            @ApiImplicitParam(name = "name", value = "菜品名称", required = false)
    })
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
        dishService.updateStatus(status, ids);
        return R.success("状态修改成功");
    }

    //根据分类id查询菜品信息列表
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        List<DishDto> dishDtoList = dishService.getList(dish);
        return R.success(dishDtoList);
    }

}
