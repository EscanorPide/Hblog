package com.hehaoran.hblog.jwt.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author:
 * @url:
 * @date: 2026/7/19
 * @description:
 **/
public class UsernameOrPasswordNullException extends AuthenticationException {
    public UsernameOrPasswordNullException(String msg, Throwable cause) {
        super(msg, cause);
    }
    public UsernameOrPasswordNullException(String msg) {
        super(msg);
    }
}