package com.hehaoran.hblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hehaoran.hblog.admin.model.vo.category.SelectRspVO;
import com.hehaoran.hblog.admin.model.vo.tag.*;
import com.hehaoran.hblog.admin.service.AdminTagService;
import com.hehaoran.hblog.common.domain.dos.TagDO;
import com.hehaoran.hblog.common.domain.mapper.TagMapper;
import com.hehaoran.hblog.common.enums.ResponseCodeEnum;
import com.hehaoran.hblog.common.exception.BizException;
import com.hehaoran.hblog.common.utils.PageResponse;
import com.hehaoran.hblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author:
 * @url:
 * @date: 2026/7/20
 * @description:
 **/
@Service
@Slf4j
public class AdminTagServiceImpl implements AdminTagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public Response addTag(AddTagReqVO addTagReqVO) {
        String tagName = addTagReqVO.getName();

        // 先判断该标签是否已经存在
        TagDO tagDO = tagMapper.selectByName(tagName);

        if (Objects.nonNull(tagDO)) {
            log.warn("标签名称： {}, 此标签已存在", tagName);
            throw new BizException(ResponseCodeEnum.TAG_NAME_IS_EXISTED);
        }

        TagDO insertTagDO = TagDO.builder()
                .name(addTagReqVO.getName().trim())
                .build();

        tagMapper.insert(insertTagDO);

        return Response.success();
    }

    /**
     * 更新标签
     *
     * @param updateTagReqVO
     * @return
     */
    @Override
    public Response updateTag(UpdateTagReqVO updateTagReqVO) {
        Long tagId = updateTagReqVO.getId();
        String tagName = updateTagReqVO.getName().trim();

        // 校验标签是否存在
        TagDO tagDO = tagMapper.selectById(tagId);
        if (Objects.isNull(tagDO)) {
            log.warn("==> 标签不存在, tagId: {}", tagId);
            throw new BizException(ResponseCodeEnum.TAG_NOT_EXISTED);
        }

        // 名称未变化则直接返回
        if (StringUtils.equals(tagDO.getName(), tagName)) {
            return Response.success();
        }

        // 校验新名称是否已被其他标签占用
        TagDO existTagDO = tagMapper.selectByName(tagName);
        if (Objects.nonNull(existTagDO) && !Objects.equals(existTagDO.getId(), tagId)) {
            log.warn("标签名称： {}, 此标签已存在", tagName);
            throw new BizException(ResponseCodeEnum.TAG_NAME_IS_EXISTED);
        }

        // 执行更新
        TagDO updateTagDO = TagDO.builder()
                .id(tagId)
                .name(tagName)
                .build();
        tagMapper.updateById(updateTagDO);

        return Response.success();
    }

    @Override
    public PageResponse findTagList(FindTagPageListReqVO findTagPageListReqVO) {
        Long current = findTagPageListReqVO.getCurrent();
        Long size = findTagPageListReqVO.getSize();

        Page<TagDO> page = new Page<>(current, size);

        LambdaQueryWrapper<TagDO> wrapper = new LambdaQueryWrapper<>();

        String name = findTagPageListReqVO.getName();
        LocalDate startDate = findTagPageListReqVO.getStartDate();
        LocalDate endDate = findTagPageListReqVO.getEndDate();

        wrapper
                .like(StringUtils.isNotBlank(name), TagDO::getName, StringUtils.trim(name))
                .ge(Objects.nonNull(startDate), TagDO::getCreateTime, startDate)
                .le(Objects.nonNull(endDate), TagDO::getCreateTime, endDate)
                .orderByDesc(TagDO::getCreateTime);

        Page<TagDO> tagDOPage = tagMapper.selectPage(page, wrapper);

        List<TagDO> tagDOS = tagDOPage.getRecords();

        List<FindTagPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(tagDOS)) {
            vos = tagDOS.stream()
                    .map(tagDO -> FindTagPageListRspVO.builder()
                            .id(tagDO.getId())
                            .name(tagDO.getName())
                            .createTime(tagDO.getCreateTime())
                            .build())
                    .collect(Collectors.toList());
        }

        return PageResponse.success(tagDOPage, vos);
    }

    @Override
    public Response deleteTag(DeleteTagReqVO deleteTagReqVO) {
        Long tagId = deleteTagReqVO.getId();
        tagMapper.deleteById(tagId);
        return Response.success();
    }

    @Override
    public Response findTagSelectList() {
        List<TagDO> tagDOS = tagMapper.selectList(null);

        List<SelectRspVO> selectRspVOS = null;
        if (!CollectionUtils.isEmpty(tagDOS)) {
            selectRspVOS = tagDOS.stream()
                    .map(tagDO -> SelectRspVO.builder()
                            .label(tagDO.getName())
                            .value(tagDO.getId())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(selectRspVOS);
    }
}
