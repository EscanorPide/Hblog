package com.hehaoran.hblog.web.service;

import com.hehaoran.hblog.common.utils.PageResponse;
import com.hehaoran.hblog.common.utils.Response;
import com.hehaoran.hblog.web.model.vo.comment.AddCommentReqVO;
import com.hehaoran.hblog.web.model.vo.comment.CommentListReqVO;

public interface WebCommentService {
    Response add(AddCommentReqVO reqVO);
    PageResponse list(CommentListReqVO reqVO);
}
