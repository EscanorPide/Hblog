package com.hehaoran.hblog.admin.model.vo.comment;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class AuditCommentReqVO {
    @NotNull(message = "评论 ID 不能为空")
    private Long id;
    @NotNull(message = "审核状态不能为空")
    @Min(value = 2, message = "审核状态只能为 2 或 3")
    @Max(value = 3, message = "审核状态只能为 2 或 3")
    private Integer status;
    @Length(max = 300, message = "原因不能超过 300 个字符")
    private String reason;
}
