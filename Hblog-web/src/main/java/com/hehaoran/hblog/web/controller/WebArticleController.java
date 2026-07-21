package com.hehaoran.hblog.web.controller;

import com.hehaoran.hblog.common.aspect.ApiOperationLog;
import com.hehaoran.hblog.common.utils.Response;
import com.hehaoran.hblog.web.model.vo.article.ArticleDetailReqVO;
import com.hehaoran.hblog.web.model.vo.article.ArticleListReqVO;
import com.hehaoran.hblog.web.service.WebArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/article")
@Api(tags = "前台文章模块")
public class WebArticleController {

    @Resource
    private WebArticleService webArticleService;

    @PostMapping("/list")
    @ApiOperation("获取首页文章分页")
    @ApiOperationLog(description = "获取首页文章分页")
    public Response findArticleList(@RequestBody @Validated ArticleListReqVO reqVO) {
        return webArticleService.findArticleList(reqVO);
    }

    @PostMapping("/detail")
    @ApiOperation("获取文章详情")
    @ApiOperationLog(description = "获取文章详情")
    public Response findArticleDetail(@RequestBody @Validated ArticleDetailReqVO reqVO) {
        return webArticleService.findArticleDetail(reqVO);
    }
}
