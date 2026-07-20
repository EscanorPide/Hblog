package com.hehaoran.hblog.common.utils;

import com.hehaoran.hblog.common.config.RustFsProperties;
import com.hehaoran.hblog.common.enums.ResponseCodeEnum;
import com.hehaoran.hblog.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

/**
 * @author:
 * @url:
 * @date: 2026/7/20
 * @description: 基于 RustFS（S3）的文件上传工具类
 **/
@Component
@Slf4j
public class FileStorageUtil {

    private static final DateTimeFormatter DATE_PATH_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Autowired
    private S3Client s3Client;

    @Autowired
    private RustFsProperties rustFsProperties;

    /**
     * 上传文件，返回可访问的完整 URL
     *
     * @param file 任意文件
     * @return 完整访问地址
     */
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException(ResponseCodeEnum.FILE_NOT_FOUND);
        }

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        String extension = resolveExtension(originalFilename, contentType);
        String objectKey = buildObjectKey(extension);

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest.Builder requestBuilder = PutObjectRequest.builder()
                    .bucket(rustFsProperties.getBucketName())
                    .key(objectKey)
                    .contentLength(file.getSize());

            if (StringUtils.isNotBlank(contentType)) {
                requestBuilder.contentType(contentType);
            }

            s3Client.putObject(requestBuilder.build(), RequestBody.fromInputStream(inputStream, file.getSize()));
            String url = buildAccessUrl(objectKey);
            log.info("文件上传成功, filename={}, key={}, url={}", originalFilename, objectKey, url);
            return url;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("文件上传失败, filename={}", originalFilename, e);
            throw new BizException(ResponseCodeEnum.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * 根据对象 key 或完整 URL 删除文件
     *
     * @param keyOrUrl 对象 key，或上传时返回的完整 URL
     */
    public void delete(String keyOrUrl) {
        if (StringUtils.isBlank(keyOrUrl)) {
            return;
        }

        String objectKey = extractObjectKey(keyOrUrl);
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(rustFsProperties.getBucketName())
                    .key(objectKey)
                    .build());
            log.info("文件删除成功, key={}", objectKey);
        } catch (Exception e) {
            log.error("文件删除失败, key={}", objectKey, e);
            throw new BizException(ResponseCodeEnum.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * 生成对象存储 key：files/yyyy/MM/dd/uuid.ext
     */
    private String buildObjectKey(String extension) {
        String datePath = LocalDate.now().format(DATE_PATH_FORMATTER);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        if (StringUtils.isBlank(extension)) {
            return "files/" + datePath + "/" + uuid;
        }
        return "files/" + datePath + "/" + uuid + "." + extension;
    }

    /**
     * 拼接对外访问 URL
     */
        private String buildAccessUrl(String objectKey) {
            String prefix = StringUtils.removeEnd(rustFsProperties.getUrlPrefix(), "/");
            if (StringUtils.isBlank(prefix)) {
                prefix = StringUtils.removeEnd(rustFsProperties.getEndpoint(), "/")
                        + "/" + rustFsProperties.getBucketName();
            }
            return prefix + "/" + objectKey;
        }

    /**
     * 从完整 URL 中截取对象 key
     */
    private String extractObjectKey(String keyOrUrl) {
        if (!StringUtils.startsWithAny(keyOrUrl, "http://", "https://")) {
            return StringUtils.removeStart(keyOrUrl, "/");
        }

        String prefix = StringUtils.removeEnd(rustFsProperties.getUrlPrefix(), "/");
        if (StringUtils.isNotBlank(prefix) && keyOrUrl.startsWith(prefix + "/")) {
            return keyOrUrl.substring(prefix.length() + 1);
        }

        String bucketPrefix = StringUtils.removeEnd(rustFsProperties.getEndpoint(), "/")
                + "/" + rustFsProperties.getBucketName() + "/";
        if (keyOrUrl.startsWith(bucketPrefix)) {
            return keyOrUrl.substring(bucketPrefix.length());
        }

        int index = keyOrUrl.indexOf("/files/");
        if (index >= 0) {
            return keyOrUrl.substring(index + 1);
        }
        return keyOrUrl;
    }

    private String resolveExtension(String originalFilename, String contentType) {
        if (StringUtils.isNotBlank(originalFilename) && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf('.') + 1)
                    .toLowerCase(Locale.ROOT);
        }
        if (StringUtils.isBlank(contentType) || !contentType.contains("/")) {
            return "";
        }
        String subtype = contentType.substring(contentType.indexOf('/') + 1).toLowerCase(Locale.ROOT);
        // 去掉可能的参数，如 application/vnd.openxmlformats... 保持原样
        int semicolon = subtype.indexOf(';');
        if (semicolon >= 0) {
            subtype = subtype.substring(0, semicolon);
        }
        if ("jpeg".equals(subtype)) {
            return "jpg";
        }
        // 复杂 MIME 不再当扩展名
        if (subtype.contains(".") || subtype.contains("+") || subtype.length() > 10) {
            return "";
        }
        return subtype;
    }
}
