package com.hehaoran.hblog.web.model.vo.comment;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentRspVO {
    private Long id;
    private String content;
    private String avatar;
    private String nickname;
    private String website;
    private String routerUrl;
    private LocalDateTime createTime;
    private Long replyCommentId;
    private Long parentCommentId;
}
