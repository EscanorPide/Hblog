package com.hehaoran.hblog.web.service.impl;

import com.google.common.collect.Lists;
import com.hehaoran.hblog.common.utils.PageResponse;
import com.hehaoran.hblog.common.utils.Response;
import com.hehaoran.hblog.search.LuceneHelper;
import com.hehaoran.hblog.search.config.LuceneProperties;
import com.hehaoran.hblog.search.index.ArticleIndex;
import com.hehaoran.hblog.web.model.vo.search.SearchArticlePageListReqVO;
import com.hehaoran.hblog.web.model.vo.search.SearchArticlePageListRspVO;
import com.hehaoran.hblog.web.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author:
 * @url:
 * @date: 2026/7/21
 * @description:
 **/
@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

    @Autowired
    private LuceneProperties luceneProperties;
    @Autowired
    private LuceneHelper luceneHelper;

    @Override
    public Response searchArticlePageList(SearchArticlePageListReqVO searchArticlePageListReqVO) {
        int current = Math.toIntExact(searchArticlePageListReqVO.getCurrent());
        int size = Math.toIntExact(searchArticlePageListReqVO.getSize());

        // 文章索引存放目录
        String articleIndexDir = luceneProperties.getIndexDir() + File.separator + ArticleIndex.NAME;
        // 查询关键词
        String word = searchArticlePageListReqVO.getWord();

        // 想要搜索的文档字段（这里指定对文章标题、摘要进行检索，任何一个字段包含该关键词，都会被搜索到）
        String[] columns = {ArticleIndex.COLUMN_TITLE, ArticleIndex.COLUMN_SUMMARY};
        // 查询总记录数
        long total = luceneHelper.searchTotal(articleIndexDir, word, columns);

        // 执行搜索（分页查询 + 标题高亮），返回普通 Map，不依赖 Lucene 类型
        List<Map<String, String>> documents = luceneHelper.search(articleIndexDir, word, columns, current, size);

        // 若未查询到相关文档，直接 return
        if (CollectionUtils.isEmpty(documents)) {
            return PageResponse.success(total, current, size, null);
        }

        // 返参 VO
        List<SearchArticlePageListRspVO> vos = Lists.newArrayList();
        documents.forEach(document -> {
            try {
                String id = document.get(ArticleIndex.COLUMN_ID);
                if (StringUtils.isBlank(id)) {
                    return;
                }
                SearchArticlePageListRspVO vo = SearchArticlePageListRspVO.builder()
                        .id(Long.valueOf(id))
                        .title(document.get(ArticleIndex.COLUMN_TITLE))
                        .summary(document.get(ArticleIndex.COLUMN_SUMMARY))
                        .cover(document.get(ArticleIndex.COLUMN_COVER))
                        .createDate(document.get(ArticleIndex.COLUMN_CREATE_TIME))
                        .build();
                vos.add(vo);
            } catch (Exception e) {
                log.error("文档转换错误: ", e);
            }
        });

        return PageResponse.success(total, current, size, vos);
    }
}
