package com.hehaoran.hblog.admin.controller;

import com.hehaoran.hblog.admin.model.vo.user.RegisterReqVO;
import com.hehaoran.hblog.admin.service.AdminUserService;
import com.hehaoran.hblog.common.aspect.ApiOperationLog;
import com.hehaoran.hblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公开认证接口（无需登录）
 */
@RestController
@Api(tags = "认证模块")
public class AuthController {

    @Autowired
    private AdminUserService adminUserService;

    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    @ApiOperationLog(description = "用户注册")
    public Response register(@RequestBody @Validated RegisterReqVO reqVO) {
        return adminUserService.register(reqVO);
    }
}
