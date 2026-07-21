package com.hehaoran.hblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hehaoran.hblog.admin.model.vo.comment.*;
import com.hehaoran.hblog.admin.service.AdminCommentService;
import com.hehaoran.hblog.common.domain.dos.CommentDO;
import com.hehaoran.hblog.common.domain.mapper.CommentMapper;
import com.hehaoran.hblog.common.utils.PageResponse;
import com.hehaoran.hblog.common.utils.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminCommentServiceImpl implements AdminCommentService {
    @Resource
    private CommentMapper commentMapper;

    @Override
    public PageResponse list(FindCommentPageListReqVO reqVO) {
        Page<CommentDO> page = new Page<>(reqVO.getCurrent(), reqVO.getSize());
        LambdaQueryWrapper<CommentDO> wrapper = new LambdaQueryWrapper<CommentDO>()
                .like(StringUtils.isNotBlank(reqVO.getNickname()), CommentDO::getNickname, StringUtils.trim(reqVO.getNickname()))
                .eq(StringUtils.isNotBlank(reqVO.getRouterUrl()), CommentDO::getRouterUrl, StringUtils.trim(reqVO.getRouterUrl()))
                .eq(reqVO.getStatus() != null, CommentDO::getStatus, reqVO.getStatus())
                .orderByDesc(CommentDO::getCreateTime);
        Page<CommentDO> result = commentMapper.selectPage(page, wrapper);
        List<FindCommentPageListRspVO> records = result.getRecords().stream().map(item -> FindCommentPageListRspVO.builder()
                .id(item.getId()).content(item.getContent()).avatar(item.getAvatar())
                .nickname(item.getNickname()).mail(item.getMail()).website(item.getWebsite())
                .routerUrl(item.getRouterUrl()).createTime(item.getCreateTime())
                .replyCommentId(item.getReplyCommentId()).parentCommentId(item.getParentCommentId())
                .reason(item.getReason()).status(item.getStatus()).build()).collect(Collectors.toList());
        return PageResponse.success(result, records);
    }

    @Override
    public Response audit(AuditCommentReqVO reqVO) {
        CommentDO comment = commentMapper.selectById(reqVO.getId());
        if (comment == null) {
            return Response.fail("评论不存在");
        }
        CommentDO update = CommentDO.builder().id(reqVO.getId()).status(reqVO.getStatus())
                .reason(reqVO.getStatus() == 3 ? StringUtils.defaultString(reqVO.getReason()).trim() : "")
                .build();
        commentMapper.updateById(update);
        return Response.success();
    }

    @Override
    public Response delete(DeleteCommentReqVO reqVO) {
        if (commentMapper.selectById(reqVO.getId()) == null) {
            return Response.fail("评论不存在");
        }
        commentMapper.deleteById(reqVO.getId());
        return Response.success();
    }
}
