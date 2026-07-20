package com.hehaoran.hblog.admin.service.impl;

import com.hehaoran.hblog.admin.model.vo.file.UploadFileRspVO;
import com.hehaoran.hblog.admin.service.AdminFileService;
import com.hehaoran.hblog.common.enums.ResponseCodeEnum;
import com.hehaoran.hblog.common.exception.BizException;
import com.hehaoran.hblog.common.utils.FileStorageUtil;
import com.hehaoran.hblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author:
 * @url:
 * @date: 2026/7/20
 * @description:
 **/
@Service
@Slf4j
public class AdminFileServiceImpl implements AdminFileService {

    @Autowired
    private FileStorageUtil fileUtil;

    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    @Override
    public Response uploadFile(MultipartFile file) {
        try {
            // 上传文件
            String url = fileUtil.uploadFile(file);

            // 构建成功返参，将图片的访问链接返回
            return Response.success(UploadFileRspVO.builder().url(url).build());
        } catch (Exception e) {
            log.error("==> 上传文件至 RustFS 错误: ", e);
            // 手动抛出业务异常，提示 “文件上传失败”
            throw new BizException(ResponseCodeEnum.FILE_UPLOAD_FAILED);
        }
    }
}
