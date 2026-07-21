package com.hehaoran.hblog.web.controller;

import com.hehaoran.hblog.common.utils.PageResponse;
import com.hehaoran.hblog.common.utils.Response;
import com.hehaoran.hblog.web.model.vo.comment.AddCommentReqVO;
import com.hehaoran.hblog.web.model.vo.comment.CommentListReqVO;
import com.hehaoran.hblog.web.service.WebCommentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/comment")
public class WebCommentController {
    @Resource
    private WebCommentService commentService;

    @PostMapping("/add")
    public Response add(@RequestBody @Validated AddCommentReqVO reqVO) {
        return commentService.add(reqVO);
    }

    @PostMapping("/list")
    public PageResponse list(@RequestBody @Validated CommentListReqVO reqVO) {
        return commentService.list(reqVO);
    }
}
