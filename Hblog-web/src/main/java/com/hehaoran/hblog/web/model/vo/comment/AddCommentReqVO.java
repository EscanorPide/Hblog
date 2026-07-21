package com.hehaoran.hblog.web.model.vo.comment;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class AddCommentReqVO {
    @NotBlank(message = "评论内容不能为空")
    @Length(max = 120, message = "评论内容不能超过 120 个字符")
    private String content;
    @Length(max = 160, message = "头像地址不能超过 160 个字符")
    private String avatar;
    @NotBlank(message = "昵称不能为空")
    @Length(max = 60, message = "昵称不能超过 60 个字符")
    private String nickname;
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Length(max = 60, message = "邮箱不能超过 60 个字符")
    private String mail;
    @Length(max = 60, message = "网站地址不能超过 60 个字符")
    private String website;
    @NotBlank(message = "评论所属路由不能为空")
    @Length(max = 60, message = "评论所属路由不能超过 60 个字符")
    private String routerUrl;
    private Long replyCommentId;
    private Long parentCommentId;
}
