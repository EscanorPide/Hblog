package com.hehaoran.hblog.admin.service;

import com.hehaoran.hblog.admin.model.vo.category.AddCategoryReqVO;
import com.hehaoran.hblog.common.utils.Response;

/**
 * @author:
 * @url:
 * @date: 2026/7/20
 * @description:
 **/
public interface AdminCategoryService {
    /**
     * 添加分类
     * @param addCategoryReqVO
     * @return
     */
    Response addCategory(AddCategoryReqVO addCategoryReqVO);
}
