package com.hehaoran.hblog.admin.model.vo.comment;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class DeleteCommentReqVO {
    @NotNull(message = "评论 ID 不能为空")
    private Long id;
}
