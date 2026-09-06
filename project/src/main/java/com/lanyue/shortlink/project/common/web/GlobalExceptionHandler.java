package com.lanyue.shortlink.project.common.web;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.lanyue.shortlink.project.common.convention.errorcode.BaseErrorCode;
import com.lanyue.shortlink.project.common.convention.exception.ClientException;
import com.lanyue.shortlink.project.common.convention.exception.RemoteException;
import com.lanyue.shortlink.project.common.convention.exception.ServiceException;
import com.lanyue.shortlink.project.common.convention.result.Result;
import com.lanyue.shortlink.project.common.convention.result.Results;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @SneakyThrows
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result<Void> methodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException ex
    ) {
        BindingResult bindingResult = ex.getBindingResult();
        FieldError firstFieldError = CollectionUtil.getFirst(bindingResult
                .getFieldErrors()
        );
        String errorMessage = Optional.ofNullable(firstFieldError)
                .map(FieldError::getDefaultMessage)
                .orElse(StrUtil.EMPTY);
        log.error("[{}] {} [ex] {}", request.getMethod(), request.getRequestURL().toString(), errorMessage, ex);
        return Results.failure(BaseErrorCode.CLIENT_ERROR.code(), errorMessage);
    }

    @ExceptionHandler(value = {ClientException.class})
    public Result<Void> clientException(HttpServletRequest request, ClientException ex
    ) {
        log.error("[{}] {} [ex] {}", request.getMethod(), request.getRequestURL().toString(), ex.getErrorMessage(), ex);
        return Results.failure(ex);
    }

    @ExceptionHandler(value = {ServiceException.class})
    public Result<Void> serviceException(HttpServletRequest request, ServiceException ex
    ) {
        log.error("[{}] {} [ex] {}", request.getMethod(), request.getRequestURL().toString(), ex.getErrorMessage(), ex);
        return Results.failure(ex);
    }

    @ExceptionHandler(value = {RemoteException.class})
    public Result<Void> remoteException(HttpServletRequest request, RemoteException ex
    ) {
        log.error("[{}] {} [ex] {}", request.getMethod(), request.getRequestURL().toString(), ex.getErrorMessage(), ex);
        return Results.failure(ex);
    }

    @ExceptionHandler(value = Throwable.class)
    public Result<Void> defaultErrorHandler(HttpServletRequest request, Throwable throwable
    ) {
        log.error("[{}] {} ", request.getMethod(), request.getRequestURL().toString(), throwable);
        return Results.failure(BaseErrorCode.SERVICE_ERROR.code(), BaseErrorCode.SERVICE_ERROR.message());
    }
}
