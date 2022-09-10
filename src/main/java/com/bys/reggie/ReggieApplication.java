package com.bys.reggie;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Administrator
 * @version 1.0
 * @description: springboot启动类
 * @date 2022/8/31 16:04
 */
@Slf4j
@MapperScan("com.bys.reggie.mapper")
@ServletComponentScan("com.bys.reggie.filter")
@SpringBootApplication
@EnableTransactionManagement
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
        log.info("项目启动成功...");
    }
}
