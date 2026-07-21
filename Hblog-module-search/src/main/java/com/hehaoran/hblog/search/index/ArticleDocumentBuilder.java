package com.hehaoran.hblog.search.index;

import com.hehaoran.hblog.common.constant.Constants;
import com.hehaoran.hblog.common.domain.dos.ArticleContentDO;
import com.hehaoran.hblog.common.domain.dos.ArticleDO;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 文章 Lucene Document 构建器
 */
public final class ArticleDocumentBuilder {

    private ArticleDocumentBuilder() {
    }

    public static Document build(ArticleDO articleDO, ArticleContentDO articleContentDO) {
        Document document = new Document();

        // id / cover / createTime 使用 StringField，保证 Term 精确匹配（增删改索引必需）
        document.add(new StringField(ArticleIndex.COLUMN_ID, String.valueOf(articleDO.getId()), Field.Store.YES));
        document.add(new TextField(ArticleIndex.COLUMN_TITLE, defaultString(articleDO.getTitle()), Field.Store.YES));
        document.add(new StringField(ArticleIndex.COLUMN_COVER, defaultString(articleDO.getCover()), Field.Store.YES));
        document.add(new TextField(ArticleIndex.COLUMN_SUMMARY, defaultString(articleDO.getSummary()), Field.Store.YES));

        String content = Objects.isNull(articleContentDO) ? "" : defaultString(articleContentDO.getContent());
        document.add(new TextField(ArticleIndex.COLUMN_CONTENT, content, Field.Store.YES));

        LocalDateTime createTime = articleDO.getCreateTime();
        String createTimeStr = Objects.isNull(createTime) ? "" : Constants.DATE_TIME_FORMATTER.format(createTime);
        document.add(new StringField(ArticleIndex.COLUMN_CREATE_TIME, createTimeStr, Field.Store.YES));

        return document;
    }

    private static String defaultString(String value) {
        return StringUtils.defaultString(value);
    }
}
