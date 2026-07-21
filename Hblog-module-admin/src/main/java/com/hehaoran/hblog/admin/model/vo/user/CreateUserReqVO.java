package com.hehaoran.hblog.admin.model.vo.user;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class CreateUserReqVO {
    @NotBlank(message = "用户名不能为空")
    @Size(max = 60, message = "用户名不能超过 60 个字符")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 72, message = "密码长度应为 8-72 个字符")
    private String password;
    @NotEmpty(message = "角色不能为空")
    private List<String> roles;
}
