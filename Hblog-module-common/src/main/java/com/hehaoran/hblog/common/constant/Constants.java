package com.hehaoran.hblog.common.constant;

import java.time.format.DateTimeFormatter;

/**
 * @author:
 * @url:
 * @date: 2026/7/21
 * @description:
 **/
public interface Constants {
    /**
     * 月-日 格式
     */
    String MONTH_DAY_FORMAT = "MM-dd";
    DateTimeFormatter MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern(MONTH_DAY_FORMAT);

    /**
     * 年-月-日 小时-分钟-秒
     */
    String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
}