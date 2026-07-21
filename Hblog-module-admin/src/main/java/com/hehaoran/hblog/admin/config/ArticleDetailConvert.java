package com.hehaoran.hblog.admin.config;

import com.hehaoran.hblog.admin.model.vo.article.FindArticleDetailRspVO;
import com.hehaoran.hblog.common.domain.dos.ArticleDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author:
 * @url:
 * @date: 2026/7/21
 * @description:
 **/
@Mapper
public interface ArticleDetailConvert {
    /**
     * 初始化 convert 实例
     */
    ArticleDetailConvert INSTANCE = Mappers.getMapper(ArticleDetailConvert.class);

    /**
     * 将 DO 转化为 VO
     * @param bean
     * @return
     */
    FindArticleDetailRspVO convertDO2VO(ArticleDO bean);

}
