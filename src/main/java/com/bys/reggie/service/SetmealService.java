package com.bys.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bys.reggie.dto.SetmealDto;
import com.bys.reggie.entity.Dish;
import com.bys.reggie.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bys.reggie.entity.SetmealDish;

import java.util.List;

/**
* @author Administrator
* @description 针对表【setmeal(套餐)】的数据库操作Service
* @createDate 2022-09-10 15:52:57
*/
public interface SetmealService extends IService<Setmeal> {

    void saveWithDish(SetmealDto setmealDto);

    Page<SetmealDto> pageInfo(int page, int pageSize, String name);

    SetmealDto getByIdWithDish(Long id);

    void updateWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);

    List<SetmealDto> getList(Setmeal setmeal);

    List<Dish> getSetmealOfDish(Long id);
}
