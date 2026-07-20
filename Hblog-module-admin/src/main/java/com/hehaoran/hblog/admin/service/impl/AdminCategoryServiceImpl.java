package com.hehaoran.hblog.admin.service.impl;

import com.hehaoran.hblog.admin.model.vo.category.AddCategoryReqVO;
import com.hehaoran.hblog.admin.service.AdminCategoryService;
import com.hehaoran.hblog.common.domain.dos.CategoryDO;
import com.hehaoran.hblog.common.domain.mapper.CategoryMapper;
import com.hehaoran.hblog.common.enums.ResponseCodeEnum;
import com.hehaoran.hblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author:
 * @url:
 * @date: 2026/7/20
 * @description:
 **/
@Service
@Slf4j
public class AdminCategoryServiceImpl implements AdminCategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加分类
     *
     * @param addCategoryReqVO
     * @return
     */
    @Override
    public Response addCategory(AddCategoryReqVO addCategoryReqVO) {
        String categoryName = addCategoryReqVO.getName();

        // 先判断该分类是否已经存在
        CategoryDO categoryDO = categoryMapper.selectByName(categoryName);

        if (Objects.nonNull(categoryDO)) {
            log.warn("分类名称： {}, 此分类已存在", categoryName);
            throw new BizException(ResponseCodeEnum.CATEGORY_NAME_IS_EXISTED);
        }

        // 构建 DO 类
        CategoryDO insertCategoryDO = CategoryDO.builder()
                .name(addCategoryReqVO.getName().trim())
                .build();

        // 执行 insert
        categoryMapper.insert(insertCategoryDO);

        return Response.success();
    }
}
