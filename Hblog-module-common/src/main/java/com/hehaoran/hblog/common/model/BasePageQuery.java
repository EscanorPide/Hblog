package com.hehaoran.hblog.common.model;

import lombok.Data;

/**
 * @author:
 * @url:
 * @date: 2026/7/20
 * @description:
 **/
@Data
public class BasePageQuery {
    /**
     * 当前页码, 默认第一页
     */
    private Long current = 1L;
    /**
     * 每页展示的数据数量，默认每页展示 10 条数据
     */
    private Long size = 10L;
}

