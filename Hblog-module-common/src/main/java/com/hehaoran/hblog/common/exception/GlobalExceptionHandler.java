package com.hehaoran.hblog.common.exception;

import com.hehaoran.hblog.common.enums.ResponseCodeEnum;
import com.hehaoran.hblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @author:
 * @url:
 * @date: 2026/7/20
 * @description: 全局异常处理
 **/
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     */
    @ExceptionHandler({BizException.class})
    @ResponseBody
    public Response<Object> handleBizException(HttpServletRequest request, BizException e) {
        log.warn("{} request fail, errorCode: {}, errorMessage: {}",
                request.getRequestURI(), e.getErrorCode(), e.getErrorMessage());
        return Response.fail(e);
    }

    /**
     * 捕获参数校验异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public Response<Object> handleMethodArgumentNotValidException(HttpServletRequest request,
                                                                  MethodArgumentNotValidException e) {
        String errorCode = ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode();
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder sb = new StringBuilder();

        Optional.ofNullable(bindingResult.getFieldErrors()).ifPresent(errors -> {
            errors.forEach(error ->
                    sb.append(error.getField())
                            .append(" ")
                            .append(error.getDefaultMessage())
                            .append(", 当前值: '")
                            .append(error.getRejectedValue())
                            .append("'; ")
            );
        });

        String errorMessage = sb.toString();
        log.warn("{} request error, errorCode: {}, errorMessage: {}",
                request.getRequestURI(), errorCode, errorMessage);

        return Response.fail(errorCode, errorMessage);
    }

    /**
     * 捕获文件上传大小超限异常
     */
    @ExceptionHandler({MaxUploadSizeExceededException.class})
    @ResponseBody
    public Response<Object> handleMaxUploadSizeExceededException(HttpServletRequest request,
                                                                 MaxUploadSizeExceededException e) {
        log.warn("{} request fail, file size exceeded: {}", request.getRequestURI(), e.getMessage());
        return Response.fail(ResponseCodeEnum.FILE_SIZE_EXCEEDED);
    }

    /**
     * 其他未知异常兜底
     */
    @ExceptionHandler({Exception.class})
    @ResponseBody
    public Response<Object> handleOtherException(HttpServletRequest request, Exception e) {
        log.error("{} request error", request.getRequestURI(), e);
        return Response.fail(ResponseCodeEnum.SYSTEM_ERROR);
    }
}
