package com.hehaoran.hblog.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author:
 * @url:
 * @date: 2026/7/19
 * @description:
 **/
@Configuration
@MapperScan("com.hehaoran.hblog.common.domain.mapper")
public class MybatisPlusConfig {
    // 类主体内容
}