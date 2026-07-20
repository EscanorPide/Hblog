package com.hehaoran.hblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author:
 * @url:
 * @date: 2026/7/19
 * @description: 响应异常码
 **/
@Getter
@AllArgsConstructor
public enum ResponseCodeEnum {

    // ========== 通用异常状态码 ==========
    SYSTEM_ERROR("10000", "出错啦，后台小哥正在努力修复中..."),
    PARAM_NOT_VALID("10001", "参数错误"),

    // ========== 业务异常状态码 ==========
    LOGIN_FAIL("20000", "登录失败"),
    USERNAME_OR_PWD_ERROR("20001", "用户名或密码错误"),
    UNAUTHORIZED("20002", "无访问权限，请先登录！"),
    USERNAME_NOT_FOUND("20003", "该用户不存在");



    private final String errorCode;
    private final String errorMessage;
}
