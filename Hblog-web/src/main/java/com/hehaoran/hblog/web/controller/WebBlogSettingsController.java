package com.hehaoran.hblog.web.controller;

import com.hehaoran.hblog.common.aspect.ApiOperationLog;
import com.hehaoran.hblog.common.utils.Response;
import com.hehaoran.hblog.web.service.WebBlogSettingsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/blog/settings")
@Api(tags = "前台博客设置模块")
public class WebBlogSettingsController {

    @Resource
    private WebBlogSettingsService webBlogSettingsService;

    @PostMapping("/detail")
    @ApiOperation("获取博客设置详情")
    @ApiOperationLog(description = "获取博客设置详情")
    public Response findDetail() {
        return webBlogSettingsService.findDetail();
    }
}
