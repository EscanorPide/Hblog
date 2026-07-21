package com.hehaoran.hblog.admin.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 删除文章事件
 */
@Getter
public class DeleteArticleEvent extends ApplicationEvent {

    /**
     * 文章 ID
     */
    private Long articleId;

    public DeleteArticleEvent(Object source, Long articleId) {
        super(source);
        this.articleId = articleId;
    }
}
