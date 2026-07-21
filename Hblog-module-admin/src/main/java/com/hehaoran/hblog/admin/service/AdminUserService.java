package com.hehaoran.hblog.admin.service;

import com.hehaoran.hblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.hehaoran.hblog.common.utils.Response;
import com.hehaoran.hblog.common.utils.PageResponse;
import com.hehaoran.hblog.admin.model.vo.user.*;

/**
 * @author:
 * @url:
 * @date: 2026/7/20
 * @description:
 **/
public interface AdminUserService {
    /**
     * 修改密码
     * @param updateAdminUserPasswordReqVO
     * @return
     */
    Response updatePassword(UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO);
    /**
     * 获取当前登录用户信息
     * @return
     */
    Response findUserInfo();
    PageResponse findUserPage(UserPageReqVO reqVO);
    Response createUser(CreateUserReqVO reqVO);
    Response updateUserRoles(UpdateUserRolesReqVO reqVO);
    Response resetUserPassword(ResetUserPasswordReqVO reqVO);
    Response deleteUser(UserIdReqVO reqVO);

    /** 公开注册（默认 user 角色） */
    Response register(RegisterReqVO reqVO);
}
