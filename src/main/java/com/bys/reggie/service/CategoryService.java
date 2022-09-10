package com.bys.reggie.service;

import com.bys.reggie.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【category(菜品及套餐分类)】的数据库操作Service
* @createDate 2022-09-10 15:52:15
*/
public interface CategoryService extends IService<Category> {

    void removeAllById(Long id);
}
