package com.hehaoran.hblog.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author:
 * @url:
 * @date: 2026/7/20
 * @description: RustFS 配置（S3 兼容）
 **/
@Data
@Component
@ConfigurationProperties(prefix = "rustfs")
public class RustFsProperties {

    /**
     * RustFS 服务地址
     */
    private String endpoint;

    /**
     * Access Key
     */
    private String accessKey;

    /**
     * Secret Key
     */
    private String secretKey;

    /**
     * 桶名称
     */
    private String bucketName;

    /**
     * Region（RustFS 不校验，SDK 必填）
     */
    private String region = "us-east-1";

    /**
     * 是否强制 path-style（RustFS 必须为 true）
     */
    private boolean pathStyleAccess = true;

    /**
     * 对外访问 URL 前缀，用于拼接文件完整地址
     */
    private String urlPrefix;
}
