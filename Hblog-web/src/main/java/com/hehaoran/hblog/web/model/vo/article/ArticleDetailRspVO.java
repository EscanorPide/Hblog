package com.hehaoran.hblog.web.model.vo.article;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ArticleDetailRspVO {

    private Long id;

    private String title;

    private String cover;

    private String summary;

    private String content;

    private LocalDateTime createTime;

    /** 阅读量 */
    private Long readNum;

    private Long categoryId;

    private String categoryName;

    private List<ArticleTagRspVO> tags;
}
