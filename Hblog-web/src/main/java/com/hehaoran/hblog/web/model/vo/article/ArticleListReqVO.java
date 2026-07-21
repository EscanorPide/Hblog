package com.hehaoran.hblog.web.model.vo.article;

import com.hehaoran.hblog.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("前台文章分页查询入参")
public class ArticleListReqVO extends BasePageQuery {

    private String title;
}
