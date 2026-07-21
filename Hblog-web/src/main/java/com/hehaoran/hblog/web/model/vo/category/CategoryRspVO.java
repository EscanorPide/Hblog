package com.hehaoran.hblog.web.model.vo.category;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryRspVO {

    private Long id;

    private String name;

    private Long articlesTotal;
}
