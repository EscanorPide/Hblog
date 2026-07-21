package com.hehaoran.hblog.admin.service;

import com.hehaoran.hblog.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import com.hehaoran.hblog.common.utils.Response;

/**
 * @author:
 * @url:
 * @date: 2026/7/20
 * @description:
 **/
public interface AdminBlogSettingsService {
    /**
     * 更新博客设置信息
     * @param updateBlogSettingsReqVO
     * @return
     */
    Response updateBlogSettings(UpdateBlogSettingsReqVO updateBlogSettingsReqVO);

    /**
     * 获取博客设置详情
     * @return
     */
    Response findDetail();
}
