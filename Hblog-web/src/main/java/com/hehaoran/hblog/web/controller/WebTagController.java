package com.hehaoran.hblog.web.controller;

import com.hehaoran.hblog.common.aspect.ApiOperationLog;
import com.hehaoran.hblog.common.utils.Response;
import com.hehaoran.hblog.web.model.vo.article.TagArticleListReqVO;
import com.hehaoran.hblog.web.service.WebArticleService;
import com.hehaoran.hblog.web.service.WebSidebarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/tag")
@Api(tags = "前台标签模块")
public class WebTagController {

    @Resource
    private WebSidebarService webSidebarService;
    @Resource
    private WebArticleService webArticleService;

    @PostMapping("/list")
    @ApiOperation("获取侧栏标签")
    @ApiOperationLog(description = "获取侧栏标签")
    public Response findTagList() {
        return webSidebarService.findTagList();
    }

    @PostMapping("/article/list")
    @ApiOperation("获取标签下文章分页")
    @ApiOperationLog(description = "获取标签下文章分页")
    public Response findArticleList(@RequestBody @Validated TagArticleListReqVO reqVO) {
        return webArticleService.findArticleListByTag(reqVO);
    }
}
