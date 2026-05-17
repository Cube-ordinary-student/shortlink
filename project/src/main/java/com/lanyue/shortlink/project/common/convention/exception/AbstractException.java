package com.lanyue.shortlink.project.common.convention.exception;

import com.lanyue.shortlink.admin.common.convention.errorcode.IErrorCode;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.io.Serial;

/**
 * 抽象项目中三类异常体系，客户端异常、服务端异常以及远程服务调用异常
 */
@Getter
public abstract class AbstractException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -4143498765480610315L;

    private final String errorCode;

    private final String errorMessage;

    public AbstractException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable);
        this.errorCode = errorCode.code();
        this.errorMessage = StringUtils.hasText(message) ? message : errorCode.message();
    }

    public AbstractException(String message, IErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode.code();
        this.errorMessage = StringUtils.hasText(message) ? message : errorCode.message();
    }

    public AbstractException(IErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode.code();
        this.errorMessage = errorCode.message();
    }

    public AbstractException(String message, Throwable throwable, String errorCode, String errorMessage) {
        super(message, throwable);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
