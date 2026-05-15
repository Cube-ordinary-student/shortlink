package com.lanyue.shortlink.admin.common.enums;

import com.lanyue.shortlink.admin.common.convention.errorcode.IErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 短链接错误码枚举
 */
@Getter
@RequiredArgsConstructor
public enum ShortLinkErrorCodeEnum implements IErrorCode {

    SHORT_LINK_NOT_EXIST("A000300", "短链接不存在"),

    SHORT_LINK_EXIST("A000301", "短链接已存在"),

    SHORT_LINK_URL_ERROR("A000302", "短链接跳转URL不合法");

    private final String code;

    private final String message;

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
