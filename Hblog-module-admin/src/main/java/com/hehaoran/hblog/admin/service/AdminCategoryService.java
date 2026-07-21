package com.hehaoran.hblog.admin.service;

import com.hehaoran.hblog.admin.model.vo.category.AddCategoryReqVO;
import com.hehaoran.hblog.admin.model.vo.category.DeleteCategoryReqVO;
import com.hehaoran.hblog.admin.model.vo.category.FindCategoryPageListReqVO;
import com.hehaoran.hblog.admin.model.vo.category.UpdateCategoryReqVO;
import com.hehaoran.hblog.common.utils.PageResponse;
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
    /**
     * 更新分类
     * @param updateCategoryReqVO
     * @return
     */
    Response updateCategory(UpdateCategoryReqVO updateCategoryReqVO);
    /**
     * 分类分页数据查询
     * @param findCategoryPageListReqVO
     * @return
     */
    PageResponse findCategoryList(FindCategoryPageListReqVO findCategoryPageListReqVO);
    /**
     * 删除分类
     * @param deleteCategoryReqVO
     * @return
     */
    Response deleteCategory(DeleteCategoryReqVO deleteCategoryReqVO);
    /**
     * 获取文章分类的 Select 列表数据
     * @return
     */
    Response findCategorySelectList();
}
