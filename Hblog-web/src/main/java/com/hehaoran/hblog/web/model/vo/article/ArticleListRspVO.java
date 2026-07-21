package com.hehaoran.hblog.web.model.vo.article;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ArticleListRspVO {

    private Long id;

    private String title;

    private String cover;

    private String summary;

    private LocalDateTime createTime;

    private Long categoryId;

    private String categoryName;

    private List<ArticleTagRspVO> tags;
}
