package com.hehaoran.hblog.web;

import com.hehaoran.hblog.common.domain.dos.UserDO;
import com.hehaoran.hblog.common.domain.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;

@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void insertTest() {
        // 密码必须存 BCrypt 密文，否则登录校验会失败
        String encodePassword = new BCryptPasswordEncoder().encode("quanxiaoha");

        UserDO userDO = UserDO.builder()
                .username("quanxiaoha")
                .password(encodePassword)
                .createTime(new Date())
                .updateTime(new Date())
                .isDeleted(false)
                .build();

        userMapper.insert(userDO);
    }
}