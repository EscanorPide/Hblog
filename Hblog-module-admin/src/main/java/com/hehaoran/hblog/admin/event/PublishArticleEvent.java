package com.hehaoran.hblog.admin.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 发布文章事件
 */
@Getter
public class PublishArticleEvent extends ApplicationEvent {

    /**
     * 文章 ID
     */
    private Long articleId;

    public PublishArticleEvent(Object source, Long articleId) {
        super(source);
        this.articleId = articleId;
    }
}
