package com.hehaoran.hblog.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author:
 * @url:
 * @date: 2026/7/17
 * @description: JSON 序列化工具类，基于 Jackson 将对象转为 JSON 字符串
 **/
@Slf4j
public class JsonUtil {
    private static final ObjectMapper INSTANCE = new ObjectMapper();
    public static String toJsonString(Object object) {
        try {
            return INSTANCE.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return object.toString();
        }
    }
}