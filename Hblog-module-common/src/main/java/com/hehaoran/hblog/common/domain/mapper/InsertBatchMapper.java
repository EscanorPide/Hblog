package com.hehaoran.hblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author:
 * @url:
 * @date: 2026/7/21
 * @description:
 **/
public interface InsertBatchMapper<T> extends BaseMapper<T> {

    // 批量插入
    int insertBatchSomeColumn(@Param("list") List<T> batchList);

}
