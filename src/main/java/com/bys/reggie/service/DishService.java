package com.bys.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bys.reggie.common.R;
import com.bys.reggie.dto.DishDto;
import com.bys.reggie.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【dish(菜品管理)】的数据库操作Service
* @createDate 2022-09-10 15:52:15
*/
public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);

    void updateWithFlavor(DishDto dishDto);

    void removeWithFlavor(List<Long> ids);

    R<DishDto> getByIdWithFlavor(Long id);

    R<Page> pageInfo(int page, int pageSize, String name);

}
