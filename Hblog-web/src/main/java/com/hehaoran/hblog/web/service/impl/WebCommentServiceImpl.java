package com.hehaoran.hblog.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hehaoran.hblog.common.domain.dos.CommentDO;
import com.hehaoran.hblog.common.domain.mapper.CommentMapper;
import com.hehaoran.hblog.common.utils.PageResponse;
import com.hehaoran.hblog.common.utils.Response;
import com.hehaoran.hblog.common.utils.SensitiveWordUtil;
import com.hehaoran.hblog.web.model.vo.comment.AddCommentReqVO;
import com.hehaoran.hblog.web.model.vo.comment.CommentListReqVO;
import com.hehaoran.hblog.web.model.vo.comment.CommentRspVO;
import com.hehaoran.hblog.web.service.WebCommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WebCommentServiceImpl implements WebCommentService {
    @Resource
    private CommentMapper commentMapper;

    @Override
    public Response add(AddCommentReqVO reqVO) {
        Long parentId = reqVO.getParentCommentId();
        Long replyId = reqVO.getReplyCommentId();
        String routerUrl = StringUtils.trim(reqVO.getRouterUrl());
        CommentDO parent = parentId == null ? null : commentMapper.selectById(parentId);
        CommentDO reply = replyId == null ? null : commentMapper.selectById(replyId);
        if (parentId != null && (parent == null || parent.getStatus() != 2
                || !StringUtils.equals(parent.getRouterUrl(), routerUrl))) {
            return Response.fail("父评论不存在或不可回复");
        }
        if (replyId != null && (reply == null || reply.getStatus() != 2
                || !StringUtils.equals(reply.getRouterUrl(), routerUrl))) {
            return Response.fail("回复的评论不存在或不可回复");
        }
        CommentDO comment = CommentDO.builder()
                .content(StringUtils.trim(reqVO.getContent()))
                .avatar(StringUtils.trimToNull(reqVO.getAvatar()))
                .nickname(StringUtils.trim(reqVO.getNickname()))
                .mail(StringUtils.trim(reqVO.getMail()))
                .website(StringUtils.trimToNull(reqVO.getWebsite()))
                .routerUrl(routerUrl)
                .replyCommentId(replyId)
                .parentCommentId(parentId)
                .reason("")
                .status(1)
                .build();
        commentMapper.insert(comment);
        return Response.success(comment.getId());
    }

    @Override
    public PageResponse list(CommentListReqVO reqVO) {
        Page<CommentDO> page = new Page<>(reqVO.getCurrent(), reqVO.getSize());
        LambdaQueryWrapper<CommentDO> wrapper = new LambdaQueryWrapper<CommentDO>()
                .eq(CommentDO::getRouterUrl, StringUtils.trim(reqVO.getRouterUrl()))
                .eq(CommentDO::getStatus, 2)
                .orderByAsc(CommentDO::getCreateTime);
        Page<CommentDO> result = commentMapper.selectPage(page, wrapper);
        List<CommentRspVO> records = result.getRecords().stream().map(item -> CommentRspVO.builder()
                .id(item.getId()).content(SensitiveWordUtil.filter(item.getContent(),'*')).avatar(item.getAvatar())
                .nickname(item.getNickname()).website(item.getWebsite())
                .routerUrl(item.getRouterUrl()).createTime(item.getCreateTime())
                .replyCommentId(item.getReplyCommentId()).parentCommentId(item.getParentCommentId())
                .build()).collect(Collectors.toList());
        return PageResponse.success(result, records);
    }
}
