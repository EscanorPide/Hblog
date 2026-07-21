package com.hehaoran.hblog.web.model.vo.article;

import com.hehaoran.hblog.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("按分类查询前台文章分页入参")
public class CategoryArticleListReqVO extends BasePageQuery {

    @NotNull(message = "分类 ID 不能为空")
    private Long categoryId;
}
