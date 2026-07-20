package com.hehaoran.hblog.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @author:
 * @url:
 * @date: 2026/7/20
 * @description: 业务异常
 **/
@Getter
@Setter
public class BizException extends RuntimeException {

    private String errorCode;
    private String errorMessage;

    public BizException(BaseExceptionInterface baseExceptionInterface) {
        this.errorCode = baseExceptionInterface.getErrorCode();
        this.errorMessage = baseExceptionInterface.getErrorMessage();
    }

    public BizException(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
