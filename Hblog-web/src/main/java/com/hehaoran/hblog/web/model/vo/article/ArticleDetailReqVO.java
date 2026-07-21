package com.hehaoran.hblog.web.model.vo.article;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("前台文章详情查询入参")
public class ArticleDetailReqVO {

    @NotNull(message = "文章 ID 不能为空")
    private Long id;
}
