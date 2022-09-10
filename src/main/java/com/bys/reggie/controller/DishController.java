package com.bys.reggie.controller;

import com.bys.reggie.common.R;
import com.bys.reggie.dto.DishDto;
import com.bys.reggie.service.DishFlavorService;
import com.bys.reggie.service.DishService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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

    /**
     * @description: 保存菜品信息
     * @param dishDto 携带口味信息的菜品信息
     * @return R<String>
     * @author Administrator
     * @date 2022/9/10 15:13
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("菜品添加成功");
    }
}
