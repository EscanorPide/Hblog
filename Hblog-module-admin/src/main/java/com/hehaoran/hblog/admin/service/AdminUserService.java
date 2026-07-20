package com.hehaoran.hblog.admin.service;

import com.hehaoran.hblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.hehaoran.hblog.common.utils.Response;

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
}
