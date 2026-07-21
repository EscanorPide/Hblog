package com.hehaoran.hblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hehaoran.hblog.common.domain.dos.UserRoleDO;

import java.util.List;
import java.util.stream.Collectors;

public interface UserRoleMapper extends BaseMapper<UserRoleDO> {
    default List<String> findRolesByUserId(Long userId) {
        return selectList(new LambdaQueryWrapper<UserRoleDO>()
                .eq(UserRoleDO::getUserId, userId)
                .select(UserRoleDO::getRole))
                .stream().map(UserRoleDO::getRole).collect(Collectors.toList());
    }

    default int deleteByUserId(Long userId) {
        return delete(new LambdaQueryWrapper<UserRoleDO>().eq(UserRoleDO::getUserId, userId));
    }
}
