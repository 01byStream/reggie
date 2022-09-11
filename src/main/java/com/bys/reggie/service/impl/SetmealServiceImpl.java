package com.bys.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bys.reggie.dto.SetmealDto;
import com.bys.reggie.entity.Category;
import com.bys.reggie.entity.Setmeal;
import com.bys.reggie.entity.SetmealDish;
import com.bys.reggie.service.CategoryService;
import com.bys.reggie.service.SetmealDishService;
import com.bys.reggie.service.SetmealService;
import com.bys.reggie.mapper.SetmealMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【setmeal(套餐)】的数据库操作Service实现
* @createDate 2022-09-10 15:52:57
*/
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
    implements SetmealService{

    @Resource
    SetmealDishService setmealDishService;

    @Resource
    CategoryService categoryService;

    /**
     * @description: 插入记录到套餐表，同时插入记录到套餐菜品关系表
     * @author Administrator
     * @date 2022/9/11 19:43
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //插入套餐表
        this.save(setmealDto);
        //插入套餐菜品关系表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * @description: 套餐信息分页查询，携带分类名称
     * @param page 分页第几页
     * @param pageSize 每页数据数量
     * @param name 套餐名称
     * @return Page
     * @author Administrator
     * @date 2022/9/11 20:04
     */
    @Transactional
    @Override
    public Page<SetmealDto> pageInfo(int page, int pageSize, String name) {
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        //查询套餐分页数据，不包含分类名称
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        this.page(setmealPage, queryWrapper);
        //拷贝分页数据，获取分类名称
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
        List<Setmeal> records = setmealPage.getRecords();
        //联表查询分类名称
        List<SetmealDto> setmealDtoList = records.stream().map(setmeal -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);
            Category category = categoryService.getById(setmeal.getCategoryId());
            setmealDto.setCategoryName(category.getName());
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtoList); //得到套餐分页数据
        return setmealDtoPage;
    }

    /**
     * @description: 根据id查询套餐信息，包括套餐分类名称以及套餐菜品信息
     * @param id 套餐id
     * @return SetmealDto
     * @author Administrator
     * @date 2022/9/11 20:35
     */
    @Transactional
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        SetmealDto setmealDto = new SetmealDto();
        //查询套餐信息
        Setmeal setmeal = this.getById(id);
        BeanUtils.copyProperties(setmeal, setmealDto);
        //查询套餐菜品信息
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishes = setmealDishService.list(setmealDishLambdaQueryWrapper);
        setmealDto.setSetmealDishes(setmealDishes);
        //查询分类名称
        Category category = categoryService.getById(setmeal.getCategoryId());
        setmealDto.setCategoryName(category.getName());
        return setmealDto;
    }

    /**
     * @description: 更新套餐信息，包括套餐的菜品信息
     * @param setmealDto 修改的套餐信息，包括菜品信息
     * @author Administrator
     * @date 2022/9/11 20:47
     */
    @Transactional
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //更新套餐表
        this.updateById(setmealDto);
        //更新套餐菜品表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //先删除套餐菜品记录
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
        //再插入新的套餐菜品记录
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * @description: 根据id批量删除套餐信息，以及套餐菜品信息
     * @param ids 套餐id列表
     * @author Administrator
     * @date 2022/9/11 21:13
     */
    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
        //删除套餐信息
        this.removeByIds(ids);
        //删除套餐菜品信息
        for (Long id : ids) {
            LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, id);
            setmealDishService.remove(setmealDishLambdaQueryWrapper);
        }
    }
}




