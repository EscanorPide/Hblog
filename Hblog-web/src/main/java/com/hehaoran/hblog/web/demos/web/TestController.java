package com.hehaoran.hblog.web.demos.web;

import com.hehaoran.hblog.common.aspect.ApiOperationLog;
import com.hehaoran.hblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.stream.Collectors;

/**
 * @author:
 * @url:
 * @date: 2026/7/17
 * @description:
 **/
@RestController
@Slf4j
@Api(tags = "首页模块")
public class TestController {

    @PostMapping("/test")
    @ApiOperationLog(description = "测试案例")
    public String test(@RequestBody User user) {
        log.info("收到请求: {}", user);
        return user.toString();
    }
    @PostMapping("/test1")
    @ApiOperationLog()
    public Response<String> test1(@RequestBody @Validated User user, BindingResult bindResult) {
        // 是否存在校验错误
        if (bindResult.hasErrors()) {
            // 获取校验不通过字段的提示信息
            String errorMsg = bindResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));

            return Response.fail(errorMsg);
        }

        // 返参
        return Response.success("参数没有任何问题");
    }
    @PostMapping("/test2")
    @ApiOperationLog()
    @ApiOperation(value = "测试接口")
    public Response<String> test2(@RequestBody User user) {
        return Response.success();
    }


}
