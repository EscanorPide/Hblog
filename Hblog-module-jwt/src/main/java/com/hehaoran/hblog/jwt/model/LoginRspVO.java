package com.hehaoran.hblog.jwt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author:
 * @url:
 * @date: 2026/7/19
 * @description:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRspVO {
    // 类主体内容
    /**
     * Token 值
     */
    private String token;

}