package com.hehaoran.hblog.admin.model.vo.user;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class UserIdReqVO {
    @NotNull(message = "用户 ID 不能为空")
    private Long id;
}
