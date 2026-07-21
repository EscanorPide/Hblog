package com.hehaoran.hblog.admin.model.vo.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hehaoran.hblog.common.constant.Constants;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class UserRspVO {
    private Long id;
    private String username;
    private List<String> roles;
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT, timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT, timezone = "GMT+8")
    private Date updateTime;
}
