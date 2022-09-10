package com.bys.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author Administrator
 * @version 1.0
 * @description: 自定义元数据处理器
 * @date 2022/9/7 20:18
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    //执行插入操作，自动填充
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("执行插入操作，自动填充...");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    //执行更新操作，自动填充
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("执行更新操作，自动填充...");
        log.info(metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
