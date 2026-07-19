package com.hehaoran.hblog.common.aspect;

import java.lang.annotation.*;

/**
 * @author:
 * @url:
 * @date: 2026/7/17
 * @description:
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ApiOperationLog {
    /**
     * API 功能描述
     *
     * @return
     */
    String description() default "";
}