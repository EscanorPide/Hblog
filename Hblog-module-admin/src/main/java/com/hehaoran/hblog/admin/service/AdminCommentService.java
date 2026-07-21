package com.hehaoran.hblog.admin.service;

import com.hehaoran.hblog.admin.model.vo.comment.AuditCommentReqVO;
import com.hehaoran.hblog.admin.model.vo.comment.DeleteCommentReqVO;
import com.hehaoran.hblog.admin.model.vo.comment.FindCommentPageListReqVO;
import com.hehaoran.hblog.common.utils.PageResponse;
import com.hehaoran.hblog.common.utils.Response;

public interface AdminCommentService {
    PageResponse list(FindCommentPageListReqVO reqVO);
    Response audit(AuditCommentReqVO reqVO);
    Response delete(DeleteCommentReqVO reqVO);
}
