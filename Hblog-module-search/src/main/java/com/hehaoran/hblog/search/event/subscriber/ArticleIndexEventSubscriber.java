package com.hehaoran.hblog.search.event.subscriber;

import com.hehaoran.hblog.admin.event.DeleteArticleEvent;
import com.hehaoran.hblog.admin.event.PublishArticleEvent;
import com.hehaoran.hblog.admin.event.UpdateArticleEvent;
import com.hehaoran.hblog.common.domain.dos.ArticleContentDO;
import com.hehaoran.hblog.common.domain.dos.ArticleDO;
import com.hehaoran.hblog.common.domain.mapper.ArticleContentMapper;
import com.hehaoran.hblog.common.domain.mapper.ArticleMapper;
import com.hehaoran.hblog.search.LuceneHelper;
import com.hehaoran.hblog.search.config.LuceneProperties;
import com.hehaoran.hblog.search.index.ArticleDocumentBuilder;
import com.hehaoran.hblog.search.index.ArticleIndex;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.File;
import java.util.Objects;

/**
 * 文章变更事件订阅：同步维护 Lucene 文档，保证检索与 DB 一致。
 * <p>
 * 使用 AFTER_COMMIT，避免事务未提交时异步读库导致空指针。
 */
@Component
@Slf4j
public class ArticleIndexEventSubscriber {

    @Autowired
    private LuceneProperties luceneProperties;
    @Autowired
    private LuceneHelper luceneHelper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleContentMapper articleContentMapper;

    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPublishArticleEvent(PublishArticleEvent event) {
        Long articleId = event.getArticleId();
        log.info("==> 收到发布文章事件，开始同步 Lucene 文档, articleId: {}", articleId);

        Document document = buildDocument(articleId);
        if (Objects.isNull(document)) {
            return;
        }
        luceneHelper.addDocument(getArticleIndexDir(), document);
    }

    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUpdateArticleEvent(UpdateArticleEvent event) {
        Long articleId = event.getArticleId();
        log.info("==> 收到更新文章事件，开始同步 Lucene 文档, articleId: {}", articleId);

        Document document = buildDocument(articleId);
        if (Objects.isNull(document)) {
            return;
        }
        luceneHelper.updateDocument(getArticleIndexDir(), ArticleIndex.COLUMN_ID, String.valueOf(articleId), document);
    }

    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDeleteArticleEvent(DeleteArticleEvent event) {
        Long articleId = event.getArticleId();
        log.info("==> 收到删除文章事件，开始删除 Lucene 文档, articleId: {}", articleId);
        luceneHelper.deleteDocument(getArticleIndexDir(), ArticleIndex.COLUMN_ID, String.valueOf(articleId));
    }

    private Document buildDocument(Long articleId) {
        ArticleDO articleDO = articleMapper.selectById(articleId);
        if (Objects.isNull(articleDO)) {
            log.warn("==> 同步 Lucene 失败，文章不存在, articleId: {}", articleId);
            return null;
        }

        ArticleContentDO articleContentDO = articleContentMapper.selectByArticleId(articleId);
        if (Objects.isNull(articleContentDO)) {
            log.warn("==> 同步 Lucene 失败，文章正文不存在, articleId: {}", articleId);
            return null;
        }

        return ArticleDocumentBuilder.build(articleDO, articleContentDO);
    }

    private String getArticleIndexDir() {
        return luceneProperties.getIndexDir() + File.separator + ArticleIndex.NAME;
    }
}
