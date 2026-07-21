package com.hehaoran.hblog.admin.model.vo.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RegisterReqVO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 60, message = "用户名长度为 3-60 个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 72, message = "密码长度应为 8-72 个字符")
    private String password;

    @NotBlank(message = "请再次输入密码")
    private String confirmPassword;
}
