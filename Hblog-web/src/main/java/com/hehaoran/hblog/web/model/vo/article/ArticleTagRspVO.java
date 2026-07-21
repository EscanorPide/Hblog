package com.hehaoran.hblog.web.model.vo.article;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArticleTagRspVO {

    private Long id;

    private String name;
}
