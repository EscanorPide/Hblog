package com.hehaoran.hblog.web.service;

import com.hehaoran.hblog.common.utils.Response;
import com.hehaoran.hblog.web.model.vo.search.SearchArticlePageListReqVO;

/**
 * @author:
 * @url:
 * @date: 2026/7/21
 * @description:
 **/
public interface SearchService {

    /**
     * 关键词分页搜索
     * @param searchArticlePageListReqVO
     * @return
     */
    Response searchArticlePageList(SearchArticlePageListReqVO searchArticlePageListReqVO);
}