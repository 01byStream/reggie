package com.bys.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bys.reggie.common.R;
import com.bys.reggie.entity.Category;
import com.bys.reggie.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description: 分类管理控制器
 * @date 2022/9/8 15:44
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Resource
    CategoryService categoryService;

    //分类分页查询
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        //新建分页信息
        Page<Category> categoryPage = new Page<>(page, pageSize);
        //新建查询，按sort升序排列
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        //执行分页查询
        categoryService.page(categoryPage, queryWrapper);
        //返回分页结果
        return R.success(categoryPage);
    }

    //新增分类
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("添加成功");
    }

    //修改分类
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    //删除分类
    @DeleteMapping
    public R<String> delete(@RequestParam Long ids) {
        categoryService.removeAllById(ids);
        return R.success("删除成功");
    }

    //编辑页面反查详情
    @GetMapping("/{id}")
    public R<String> getById(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        if (category == null) {
            return R.error("查询失败");
        }
        return R.success("查询成功");
    }

    //根据条件查询分类信息列表
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort);
        //执行查询
        List<Category> categories = categoryService.list(queryWrapper);
        if (categories == null) {
            return R.error("没有找到分类信息");
        }
        return R.success(categories);
    }

}
