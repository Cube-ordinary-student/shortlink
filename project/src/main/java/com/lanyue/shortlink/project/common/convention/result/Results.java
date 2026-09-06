package com.lanyue.shortlink.project.common.convention.result;

import com.lanyue.shortlink.project.common.convention.errorcode.BaseErrorCode;
import com.lanyue.shortlink.project.common.convention.errorcode.IErrorCode;
import com.lanyue.shortlink.project.common.convention.exception.AbstractException;

import java.util.Optional;

/**
 * 全局返回对象构造器
 */
public final class Results {

    public static <T> Result<T> success() {
        return new Result<T>()
                .setCode(Result.SUCCESS_CODE)
                .setMessage("操作成功");
    }

    public static <T> Result<T> success(T data) {
        return new Result<T>()
                .setCode(Result.SUCCESS_CODE)
                .setMessage("操作成功")
                .setData(data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<T>()
                .setCode(Result.SUCCESS_CODE)
                .setMessage(message)
                .setData(data);
    }

    public static Result<Void> failure() {
        return new Result<Void>()
                .setCode(BaseErrorCode.SERVICE_ERROR.code())
                .setMessage(BaseErrorCode.SERVICE_ERROR.message());
    }

    public static Result<Void> failure(String errorCode, String errorMessage) {
        return new Result<Void>()
                .setCode(errorCode)
                .setMessage(errorMessage);
    }

    public static <T> Result<T> failure(IErrorCode errorCode) {
        return new Result<T>()
                .setCode(errorCode.code())
                .setMessage(errorCode.message());
    }

    public static Result<Void> failure(AbstractException e) {
        String errorCode = Optional.ofNullable(e.getErrorCode())
                .orElse(BaseErrorCode.SERVICE_ERROR.code());
        String errorMessage = Optional.ofNullable(e.getErrorMessage())
                .orElse(BaseErrorCode.SERVICE_ERROR.message());
        return new Result<Void>()
                .setCode(errorCode)
                .setMessage(errorMessage);
    }
}
