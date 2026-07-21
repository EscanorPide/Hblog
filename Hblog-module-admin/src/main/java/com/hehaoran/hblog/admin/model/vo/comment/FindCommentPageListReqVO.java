package com.hehaoran.hblog.admin.model.vo.comment;

import com.hehaoran.hblog.common.model.BasePageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FindCommentPageListReqVO extends BasePageQuery {
    private String nickname;
    private String routerUrl;
    private Integer status;
}
