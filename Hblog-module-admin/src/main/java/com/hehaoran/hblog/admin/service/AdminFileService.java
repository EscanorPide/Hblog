package com.hehaoran.hblog.admin.service;

import com.hehaoran.hblog.common.utils.Response;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author:
 * @url:
 * @date: 2026/7/20
 * @description:
 **/
public interface AdminFileService {
    /**
     * 上传文件
     * @param file
     * @return
     */
    Response uploadFile(MultipartFile file);
}
