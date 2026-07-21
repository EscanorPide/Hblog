package com.hehaoran.hblog.admin.model.vo.user;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "修改用户密码 VO")
public class UpdateAdminUserPasswordReqVO {

    @Deprecated
    @ApiModelProperty(value = "用户名")
    private String username;

    @NotBlank(message = "密码不能为空")
    @javax.validation.constraints.Size(min = 8, max = 72, message = "密码长度应为 8-72 个字符")
    @ApiModelProperty(value = "密码")
    private String password;
}
