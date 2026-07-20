package com.hehaoran.hblog.admin.controller;

import com.hehaoran.hblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.hehaoran.hblog.admin.service.AdminUserService;
import com.hehaoran.hblog.common.aspect.ApiOperationLog;
import com.hehaoran.hblog.common.domain.dos.UserDO;
import com.hehaoran.hblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author:
 * @url:
 * @date: 2026/7/20
 * @description:
 **/
@RestController
@RequestMapping("/admin")
@Api(tags = "Admin 用户模块")
public class AdminUserController {
    // 类主体内容
    @Autowired
    private AdminUserService adminUserService;

    @PostMapping("/password/update")
    @ApiOperation(value = "修改用户密码")
    @ApiOperationLog(description = "修改用户密码")
    public Response updatePassword(@RequestBody UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO) throws Exception{
        return adminUserService.updatePassword(updateAdminUserPasswordReqVO);
    }
    @PostMapping("/user/info")
    @ApiOperation(value = "获取用户的信息")
    @ApiOperationLog(description = "获取用户的信息")
    public Response<UserDO> findUserInfo(){
        return adminUserService.findUserInfo();
    }

}