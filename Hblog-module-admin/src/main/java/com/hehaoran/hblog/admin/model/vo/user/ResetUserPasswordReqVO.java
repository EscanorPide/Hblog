package com.hehaoran.hblog.admin.model.vo.user;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ResetUserPasswordReqVO {
    @NotNull(message = "用户 ID 不能为空")
    private Long id;
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 72, message = "密码长度应为 8-72 个字符")
    private String password;
}
