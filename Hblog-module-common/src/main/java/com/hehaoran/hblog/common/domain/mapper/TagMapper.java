package com.hehaoran.hblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hehaoran.hblog.common.domain.dos.TagDO;

/**
 * @author:
 * @url:
 * @date: 2026/7/20
 * @description:
 **/
public interface TagMapper extends BaseMapper<TagDO> {

    /**
     * 根据标签名查询
     *
     * @param tagName
     * @return
     */
    default TagDO selectByName(String tagName) {
        LambdaQueryWrapper<TagDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TagDO::getName, tagName);
        return selectOne(wrapper);
    }
}
