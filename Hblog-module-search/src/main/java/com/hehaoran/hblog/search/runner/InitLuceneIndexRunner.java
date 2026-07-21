package com.hehaoran.hblog.search.runner;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.hehaoran.hblog.common.domain.dos.ArticleContentDO;
import com.hehaoran.hblog.common.domain.dos.ArticleDO;
import com.hehaoran.hblog.common.domain.mapper.ArticleContentMapper;
import com.hehaoran.hblog.common.domain.mapper.ArticleMapper;
import com.hehaoran.hblog.search.LuceneHelper;
import com.hehaoran.hblog.search.config.LuceneProperties;
import com.hehaoran.hblog.search.index.ArticleDocumentBuilder;
import com.hehaoran.hblog.search.index.ArticleIndex;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * @author:
 * @url:
 * @date: 2026/7/21
 * @description: 项目启动时全量初始化文章 Lucene 索引
 **/
@Component
@Slf4j
public class InitLuceneIndexRunner implements CommandLineRunner {

    @Autowired
    private LuceneProperties luceneProperties;
    @Autowired
    private LuceneHelper luceneHelper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleContentMapper articleContentMapper;

    @Override
    public void run(String... args) {
        log.info("==> 开始初始化 Lucene 索引...");

        // 若配置文件中未配置索引存放目录，日志提示错误信息
        if (StringUtils.isBlank(luceneProperties.getIndexDir())) {
            log.error("==> 未指定 Lucene 索引存放位置，需在 application.yml 文件中添加路径配置...");
            return;
        }

        // 查询未删除的文章（兼容 is_deleted 为 null 的历史数据）
        List<ArticleDO> articleDOS = articleMapper.selectList(
                Wrappers.<ArticleDO>lambdaQuery()
                        .and(wrapper -> wrapper.eq(ArticleDO::getIsDeleted, false)
                                .or()
                                .isNull(ArticleDO::getIsDeleted))
        );

        // 未发布文章，则不创建 lucene 索引
        if (articleDOS.isEmpty()) {
            log.info("==> 未发布任何文章，暂不创建 Lucene 索引...");
            return;
        }

        // 文章索引存放目录， 如 /app/weblog/lucene-index/article
        String articleIndexDir = luceneProperties.getIndexDir() + File.separator + ArticleIndex.NAME;

        List<Document> documents = Lists.newArrayList();
        articleDOS.forEach(articleDO -> {
            Long articleId = articleDO.getId();

            // 查询文章正文（正文可能为空，需做空指针防护）
            ArticleContentDO articleContentDO = articleContentMapper.selectByArticleId(articleId);
            if (Objects.isNull(articleContentDO)) {
                log.warn("==> 文章正文不存在，跳过索引, articleId: {}", articleId);
                return;
            }

            documents.add(ArticleDocumentBuilder.build(articleDO, articleContentDO));
        });

        if (documents.isEmpty()) {
            log.info("==> 无可索引文章文档，暂不创建 Lucene 索引...");
            return;
        }

        // 创建索引
        luceneHelper.createIndex(articleIndexDir, documents);

        log.info("==> 结束初始化 Lucene 索引, 文档数: {}", documents.size());
    }
}
