package com.hehaoran.hblog.admin.model.vo.user;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UpdateUserRolesReqVO {
    @NotNull(message = "用户 ID 不能为空")
    private Long id;
    @NotEmpty(message = "角色不能为空")
    private List<String> roles;
}
