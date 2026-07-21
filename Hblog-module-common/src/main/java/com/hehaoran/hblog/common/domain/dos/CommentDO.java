package com.hehaoran.hblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_comment")
public class CommentDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String content;
    private String avatar;
    private String nickname;
    private String mail;
    private String website;
    private String routerUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer isDeleted;
    private Long replyCommentId;
    private Long parentCommentId;
    private String reason;
    private Integer status;
}
