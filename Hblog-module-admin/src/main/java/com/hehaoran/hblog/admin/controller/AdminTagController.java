package com.hehaoran.hblog.admin.controller;

import com.hehaoran.hblog.admin.model.vo.tag.AddTagReqVO;
import com.hehaoran.hblog.admin.model.vo.tag.DeleteTagReqVO;
import com.hehaoran.hblog.admin.model.vo.tag.FindTagPageListReqVO;
import com.hehaoran.hblog.admin.service.AdminTagService;
import com.hehaoran.hblog.common.aspect.ApiOperationLog;
import com.hehaoran.hblog.common.utils.PageResponse;
import com.hehaoran.hblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author:
 * @url:
 * @date: 2026/7/20
 * @description:
 **/
@RestController
@RequestMapping("/admin")
@Api(tags = "Admin 标签模块")
public class AdminTagController {

    @Autowired
    private AdminTagService tagService;

    @PostMapping("/tag/add")
    @ApiOperation(value = "添加标签")
    @ApiOperationLog(description = "添加标签")
    public Response addTag(@RequestBody @Validated AddTagReqVO addTagReqVO) {
        return tagService.addTag(addTagReqVO);
    }

    @PostMapping("/tag/list")
    @ApiOperation(value = "标签分页数据获取")
    @ApiOperationLog(description = "标签分页数据获取")
    public PageResponse findTagList(@RequestBody @Validated FindTagPageListReqVO findTagPageListReqVO) {
        return tagService.findTagList(findTagPageListReqVO);
    }

    @PostMapping("/tag/delete")
    @ApiOperation(value = "删除标签")
    @ApiOperationLog(description = "删除标签")
    public Response deleteTag(@RequestBody @Validated DeleteTagReqVO deleteTagReqVO) {
        return tagService.deleteTag(deleteTagReqVO);
    }

    @PostMapping("/tag/select/list")
    @ApiOperation(value = "标签 Select 下拉列表数据获取")
    @ApiOperationLog(description = "标签 Select 下拉列表数据获取")
    public Response findTagSelectList() {
        return tagService.findTagSelectList();
    }
}
