package com.hehaoran.hblog.common.enums;

import com.hehaoran.hblog.common.exception.BaseExceptionInterface;
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
public enum ResponseCodeEnum implements BaseExceptionInterface {

    // ========== 通用异常状态码 ==========
    SYSTEM_ERROR("10000", "出错啦，后台小哥正在努力修复中..."),
    PARAM_NOT_VALID("10001", "参数错误"),

    // ========== 业务异常状态码 ==========
    LOGIN_FAIL("20000", "登录失败"),
    USERNAME_OR_PWD_ERROR("20001", "用户名或密码错误"),
    UNAUTHORIZED("20002", "无访问权限，请先登录！"),
    USERNAME_NOT_FOUND("20003", "该用户不存在"),
    USERNAME_ALREADY_EXISTS("20004", "该用户名已存在"),
    ROLE_NOT_VALID("20012", "角色不合法"),
    CANNOT_DELETE_CURRENT_USER("20013", "不能删除当前登录用户"),
    PASSWORD_NOT_MATCH("20014", "两次输入的密码不一致"),
    REGISTER_FAIL("20015", "注册失败"),
    CATEGORY_NAME_IS_EXISTED("20005", "该分类已存在，请勿重复添加！"),
    TAG_NAME_IS_EXISTED("20006", "该标签已存在，请勿重复添加！"),
    FILE_UPLOAD_FAILED("20007", "文件上传失败！"),
    FILE_SIZE_EXCEEDED("20008", "上传文件过大，单个文件不能超过 10MB！"),
    FILE_NOT_FOUND("20009", "文件不能为空！"),
    CATEGORY_NOT_EXISTED("20010", "提交的分类不存在！"),
    ARTICLE_NOT_FOUND("20010", "该文章不存在！"),
    TAG_NOT_EXISTED("20011", "提交的标签不存在！");





    private final String errorCode;
    private final String errorMessage;
}
