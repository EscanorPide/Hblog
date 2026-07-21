package com.hehaoran.hblog.admin.controller;

import com.hehaoran.hblog.admin.model.vo.comment.AuditCommentReqVO;
import com.hehaoran.hblog.admin.model.vo.comment.DeleteCommentReqVO;
import com.hehaoran.hblog.admin.model.vo.comment.FindCommentPageListReqVO;
import com.hehaoran.hblog.admin.service.AdminCommentService;
import com.hehaoran.hblog.common.aspect.ApiOperationLog;
import com.hehaoran.hblog.common.utils.PageResponse;
import com.hehaoran.hblog.common.utils.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/admin/comment")
public class AdminCommentController {
    @Resource
    private AdminCommentService commentService;

    @PostMapping("/list")
    @ApiOperationLog(description = "评论分页列表")
    public PageResponse list(@RequestBody FindCommentPageListReqVO reqVO) {
        return commentService.list(reqVO);
    }

    @PostMapping("/audit")
    @ApiOperationLog(description = "审核评论")
    public Response audit(@RequestBody @Validated AuditCommentReqVO reqVO) {
        return commentService.audit(reqVO);
    }

    @PostMapping("/delete")
    @ApiOperationLog(description = "删除评论")
    public Response delete(@RequestBody @Validated DeleteCommentReqVO reqVO) {
        return commentService.delete(reqVO);
    }
}
