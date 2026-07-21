package com.hehaoran.hblog.admin.service;

import com.hehaoran.hblog.admin.model.vo.tag.AddTagReqVO;
import com.hehaoran.hblog.admin.model.vo.tag.DeleteTagReqVO;
import com.hehaoran.hblog.admin.model.vo.tag.FindTagPageListReqVO;
import com.hehaoran.hblog.admin.model.vo.tag.UpdateTagReqVO;
import com.hehaoran.hblog.common.utils.PageResponse;
import com.hehaoran.hblog.common.utils.Response;

/**
 * @author:
 * @url:
 * @date: 2026/7/20
 * @description:
 **/
public interface AdminTagService {

    /**
     * 添加标签
     *
     * @param addTagReqVO
     * @return
     */
    Response addTag(AddTagReqVO addTagReqVO);

    /**
     * 更新标签
     *
     * @param updateTagReqVO
     * @return
     */
    Response updateTag(UpdateTagReqVO updateTagReqVO);

    /**
     * 标签分页数据查询
     *
     * @param findTagPageListReqVO
     * @return
     */
    PageResponse findTagList(FindTagPageListReqVO findTagPageListReqVO);

    /**
     * 删除标签
     *
     * @param deleteTagReqVO
     * @return
     */
    Response deleteTag(DeleteTagReqVO deleteTagReqVO);

    /**
     * 获取文章标签的 Select 列表数据
     *
     * @return
     */
    Response findTagSelectList();
}
