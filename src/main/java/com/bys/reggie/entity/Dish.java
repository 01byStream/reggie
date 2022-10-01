package com.bys.reggie.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 菜品管理
 * @TableName dish
 */
@TableName(value ="dish")
@Data
@ApiModel("菜品")
public class Dish implements Serializable {
    /**
     * 主键
     */
    @TableId
    @ApiModelProperty("主键")
    private Long id;

    /**
     * 菜品名称
     */
    @ApiModelProperty("菜品名称")
    private String name;

    /**
     * 菜品分类id
     */
    @ApiModelProperty("所属分类id")
    private Long categoryId;

    /**
     * 菜品价格
     */
    @ApiModelProperty("价格")
    private BigDecimal price;

    /**
     * 商品码
     */
    @ApiModelProperty("商品码")
    private String code;

    /**
     * 图片
     */
    @ApiModelProperty("图片")
    private String image;

    /**
     * 描述信息
     */
    @ApiModelProperty("描述信息")
    private String description;

    /**
     * 0 停售 1 起售
     */
    @ApiModelProperty("状态：起售/停售")
    private Integer status;

    /**
     * 顺序
     */
    @ApiModelProperty("顺序")
    private Integer sort;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    /**
     * 修改人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    /**
     * 是否删除
     */
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}