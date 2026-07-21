package com.hehaoran.hblog.web.service;

import com.hehaoran.hblog.common.utils.Response;
import com.hehaoran.hblog.web.model.vo.article.ArticleDetailReqVO;
import com.hehaoran.hblog.web.model.vo.article.ArticleListReqVO;
import com.hehaoran.hblog.web.model.vo.article.CategoryArticleListReqVO;
import com.hehaoran.hblog.web.model.vo.article.TagArticleListReqVO;

public interface WebArticleService {

    Response findArticleList(ArticleListReqVO reqVO);

    Response findArticleDetail(ArticleDetailReqVO reqVO);

    Response findArticleListByCategory(CategoryArticleListReqVO reqVO);

    Response findArticleListByTag(TagArticleListReqVO reqVO);
}
