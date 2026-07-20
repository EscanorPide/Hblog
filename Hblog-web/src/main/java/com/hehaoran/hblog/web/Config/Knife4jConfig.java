package com.hehaoran.hblog.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * Knife4j / Swagger 统一配置（所有接口文档分组都放这里）
 */
@Configuration
@EnableSwagger2WebMvc
public class Knife4jConfig {

    @Bean("webApi")
    public Docket webApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo("Hblog 博客前台接口文档", "前台展示相关接口"))
                .groupName("Web 前台接口")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.hehaoran.hblog.web"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean("adminApi")
    public Docket adminApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo("Hblog 博客后台接口文档", "Admin 管理端接口"))
                .groupName("Admin 管理接口")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.hehaoran.hblog.admin.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo(String title, String description) {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .version("1.0")
                .build();
    }
}
