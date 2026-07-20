package com.hehaoran.hblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hehaoran.hblog.common.domain.dos.UserDO;

import java.util.Date;

/**
 * @author:
 * @url:
 * @date: 2026/7/19
 * @description:
 **/
public interface UserMapper extends BaseMapper<UserDO> {
    default UserDO findByUsername(String username) {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUsername, username);
        return selectOne(wrapper);
    }

    default int updatePasswordByUsername(String username, String password) {
        LambdaUpdateWrapper<UserDO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(UserDO::getPassword, password);
        wrapper.set(UserDO::getUpdateTime, new Date());
        wrapper.eq(UserDO::getUsername, username);
        return update(null, wrapper);
    }
}
