package com.bys.reggie.dto;

import com.bys.reggie.entity.Setmeal;
import com.bys.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
