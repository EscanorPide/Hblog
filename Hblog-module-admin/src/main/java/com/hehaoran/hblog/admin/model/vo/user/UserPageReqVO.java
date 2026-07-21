package com.hehaoran.hblog.admin.model.vo.user;

import com.hehaoran.hblog.common.model.BasePageQuery;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class UserPageReqVO extends BasePageQuery {
    private String username;
    private String role;

    @Override
    @Min(value = 1, message = "页码不能小于 1")
    public Long getCurrent() { return super.getCurrent(); }

    @Override
    @Min(value = 1, message = "每页条数不能小于 1")
    @Max(value = 100, message = "每页条数不能大于 100")
    public Long getSize() { return super.getSize(); }
}
