package com.hehaoran.hblog.admin.model.vo.comment;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class FindCommentPageListRspVO {
    private Long id;
    private String content;
    private String avatar;
    private String nickname;
    private String mail;
    private String website;
    private String routerUrl;
    private LocalDateTime createTime;
    private Long replyCommentId;
    private Long parentCommentId;
    private String reason;
    private Integer status;
}
