package com.hehaoran.hblog.web.controller;

import com.hehaoran.hblog.common.aspect.ApiOperationLog;
import com.hehaoran.hblog.common.utils.Response;
import com.hehaoran.hblog.web.model.vo.article.CategoryArticleListReqVO;
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
@RequestMapping("/category")
@Api(tags = "前台分类模块")
public class WebCategoryController {

    @Resource
    private WebSidebarService webSidebarService;
    @Resource
    private WebArticleService webArticleService;

    @PostMapping("/list")
    @ApiOperation("获取侧栏分类")
    @ApiOperationLog(description = "获取侧栏分类")
    public Response findCategoryList() {
        return webSidebarService.findCategoryList();
    }

    @PostMapping("/article/list")
    @ApiOperation("获取分类下文章分页")
    @ApiOperationLog(description = "获取分类下文章分页")
    public Response findArticleList(@RequestBody @Validated CategoryArticleListReqVO reqVO) {
        return webArticleService.findArticleListByCategory(reqVO);
    }
}
