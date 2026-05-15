package com.lanyue.shortlink.admin.common.convention.exception;

import com.lanyue.shortlink.admin.common.convention.errorcode.BaseErrorCode;
import com.lanyue.shortlink.admin.common.convention.errorcode.IErrorCode;

/**
 * 服务端异常
 */
public class ServiceException extends AbstractException {

    public ServiceException(String message) {
        this(message, null, BaseErrorCode.SERVICE_ERROR);
    }

    public ServiceException(IErrorCode errorCode) {
        this(null, errorCode);
    }

    public ServiceException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public ServiceException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    @Override
    public String toString() {
        return "ServiceException{" +
                "errorCode='" + getErrorCode() + '\'' +
                ", errorMessage='" + getErrorMessage() + '\'' +
                '}';
    }
}
