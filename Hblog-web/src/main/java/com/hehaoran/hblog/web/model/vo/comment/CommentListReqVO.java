package com.hehaoran.hblog.web.model.vo.comment;

import com.hehaoran.hblog.common.model.BasePageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentListReqVO extends BasePageQuery {
    @NotBlank(message = "评论所属路由不能为空")
    private String routerUrl;
}
