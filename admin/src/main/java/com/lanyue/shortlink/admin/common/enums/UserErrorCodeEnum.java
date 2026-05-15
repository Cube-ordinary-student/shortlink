package com.lanyue.shortlink.admin.common.enums;

import com.lanyue.shortlink.admin.common.convention.errorcode.IErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 用户错误码枚举
 */
@Getter
@RequiredArgsConstructor
public enum UserErrorCodeEnum implements IErrorCode {

    USER_TOKEN_VERIFICATION_FAILED("A000200", "用户Token验证失败"),

    USERNAME_VERIFICATION_FAILED("A000201", "用户名验证失败"),

    USER_PASSWORD_VERIFICATION_FAILED("A000202", "密码验证失败"),

    USER_NOT_EXIST("A000203", "用户不存在"),

    USER_NAME_EXIST("A000204", "用户名已存在"),

    USER_NAME_REGISTER_FAILED("A000205", "用户名注册失败"),

    USER_NOT_LOGIN("A000206", "用户未登录"),

    USER_ALREADY_LOGIN("A000207", "用户已登录");

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
