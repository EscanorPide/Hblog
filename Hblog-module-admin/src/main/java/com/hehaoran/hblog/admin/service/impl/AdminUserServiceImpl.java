package com.hehaoran.hblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hehaoran.hblog.admin.model.vo.user.*;
import com.hehaoran.hblog.admin.service.AdminUserService;
import com.hehaoran.hblog.common.domain.dos.UserDO;
import com.hehaoran.hblog.common.domain.dos.UserRoleDO;
import com.hehaoran.hblog.common.domain.mapper.UserMapper;
import com.hehaoran.hblog.common.domain.mapper.UserRoleMapper;
import com.hehaoran.hblog.common.enums.ResponseCodeEnum;
import com.hehaoran.hblog.common.exception.BizException;
import com.hehaoran.hblog.common.utils.PageResponse;
import com.hehaoran.hblog.common.utils.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminUserServiceImpl implements AdminUserService {
    private static final Set<String> ALLOWED_ROLES = new HashSet<>(Arrays.asList("admin", "editor", "user"));

    @Autowired private UserMapper userMapper;
    @Autowired private UserRoleMapper userRoleMapper;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public Response updatePassword(UpdateAdminUserPasswordReqVO reqVO) {
        String username = currentUsername();
        int count = userMapper.updatePasswordByUsername(username, passwordEncoder.encode(reqVO.getPassword()));
        return count == 1 ? Response.success() : Response.fail(ResponseCodeEnum.USERNAME_NOT_FOUND);
    }

    @Override
    public Response findUserInfo() {
        UserDO user = requireUserByUsername(currentUsername());
        List<String> roles = userRoleMapper.findRolesByUserId(user.getId());
        // 兼容旧数据：角色表为空时，admin 账号默认拥有 admin 权限（否则侧栏「用户管理」会消失）
        if ((roles == null || roles.isEmpty())
                && "admin".equalsIgnoreCase(StringUtils.trim(user.getUsername()))) {
            roles = Collections.singletonList("admin");
        }
        return Response.success(FindUserInfoRspVO.builder()
                .username(user.getUsername())
                .roles(roles)
                .build());
    }

    @Override
    public PageResponse findUserPage(UserPageReqVO reqVO) {
        LambdaQueryWrapper<UserDO> query = new LambdaQueryWrapper<UserDO>()
                .like(StringUtils.isNotBlank(reqVO.getUsername()), UserDO::getUsername, StringUtils.trim(reqVO.getUsername()))
                .orderByAsc(UserDO::getCreateTime);

        if (StringUtils.isNotBlank(reqVO.getRole())) {
            String role = normalizeRole(reqVO.getRole());
            List<Long> userIds = userRoleMapper.selectList(new LambdaQueryWrapper<UserRoleDO>()
                    .eq(UserRoleDO::getRole, role).select(UserRoleDO::getUserId))
                    .stream().map(UserRoleDO::getUserId).distinct().collect(Collectors.toList());
            if (userIds.isEmpty()) return PageResponse.success(new Page<>(reqVO.getCurrent(), reqVO.getSize()), Collections.emptyList());
            query.in(UserDO::getId, userIds);
        }

        Page<UserDO> page = userMapper.selectPage(new Page<>(reqVO.getCurrent(), reqVO.getSize()), query);
        List<UserRspVO> result = page.getRecords().stream().map(user -> UserRspVO.builder()
                .id(user.getId()).username(user.getUsername())
                .roles(userRoleMapper.findRolesByUserId(user.getId()))
                .createTime(user.getCreateTime()).updateTime(user.getUpdateTime()).build())
                .collect(Collectors.toList());
        return PageResponse.success(page, result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response createUser(CreateUserReqVO reqVO) {
        String username = reqVO.getUsername().trim();
        if (userMapper.findByUsername(username) != null) throw new BizException(ResponseCodeEnum.USERNAME_ALREADY_EXISTS);
        List<String> roles = normalizeRoles(reqVO.getRoles());
        UserDO user = UserDO.builder().username(username).password(passwordEncoder.encode(reqVO.getPassword())).build();
        userMapper.insert(user);
        insertRoles(user.getId(), roles);
        return Response.success(user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response register(RegisterReqVO reqVO) {
        String username = StringUtils.trim(reqVO.getUsername());
        String password = reqVO.getPassword();
        String confirm = reqVO.getConfirmPassword();
        if (!StringUtils.equals(password, confirm)) {
            throw new BizException(ResponseCodeEnum.PASSWORD_NOT_MATCH);
        }
        if (userMapper.findByUsername(username) != null) {
            throw new BizException(ResponseCodeEnum.USERNAME_ALREADY_EXISTS);
        }
        // 公开注册默认 user（后台权限由管理员另行分配）
        UserDO user = UserDO.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();
        userMapper.insert(user);
        insertRoles(user.getId(), Collections.singletonList("user"));
        return Response.success(user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response updateUserRoles(UpdateUserRolesReqVO reqVO) {
        requireUser(reqVO.getId());
        List<String> roles = normalizeRoles(reqVO.getRoles());
        userRoleMapper.deleteByUserId(reqVO.getId());
        insertRoles(reqVO.getId(), roles);
        return Response.success();
    }

    @Override
    public Response resetUserPassword(ResetUserPasswordReqVO reqVO) {
        UserDO user = requireUser(reqVO.getId());
        user.setPassword(passwordEncoder.encode(reqVO.getPassword()));
        user.setUpdateTime(new Date());
        userMapper.updateById(user);
        return Response.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteUser(UserIdReqVO reqVO) {
        UserDO user = requireUser(reqVO.getId());
        if (user.getUsername().equals(currentUsername())) throw new BizException(ResponseCodeEnum.CANNOT_DELETE_CURRENT_USER);
        userMapper.deleteById(reqVO.getId());
        userRoleMapper.deleteByUserId(reqVO.getId());
        return Response.success();
    }

    private void insertRoles(Long userId, List<String> roles) {
        for (String role : roles) userRoleMapper.insert(UserRoleDO.builder().userId(userId).role(role).build());
    }

    private List<String> normalizeRoles(List<String> roles) {
        List<String> result = roles.stream().map(this::normalizeRole).distinct().collect(Collectors.toList());
        if (result.isEmpty()) throw new BizException(ResponseCodeEnum.ROLE_NOT_VALID);
        return result;
    }

    private String normalizeRole(String role) {
        String normalized = StringUtils.lowerCase(StringUtils.trim(role), Locale.ROOT);
        if (!ALLOWED_ROLES.contains(normalized)) throw new BizException(ResponseCodeEnum.ROLE_NOT_VALID);
        return normalized;
    }

    private UserDO requireUser(Long id) {
        UserDO user = userMapper.selectById(id);
        if (user == null) throw new BizException(ResponseCodeEnum.USERNAME_NOT_FOUND);
        return user;
    }

    private UserDO requireUserByUsername(String username) {
        UserDO user = userMapper.findByUsername(username);
        if (user == null) throw new BizException(ResponseCodeEnum.USERNAME_NOT_FOUND);
        return user;
    }

    private String currentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
