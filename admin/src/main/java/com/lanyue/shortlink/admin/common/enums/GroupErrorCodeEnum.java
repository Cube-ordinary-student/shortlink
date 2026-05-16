package com.lanyue.shortlink.admin.common.enums;

import com.lanyue.shortlink.admin.common.convention.errorcode.IErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 分组错误码枚举
 */
@Getter
@RequiredArgsConstructor
public enum GroupErrorCodeEnum implements IErrorCode {

    USER_GROUP_COUNT_MAX("A000208", "分组数量已达上限"),

    USER_GROUP_NAME_EXIST("A000209", "分组名称已存在"),

    USER_GROUP_NOT_EXIST("A000210", "分组不存在"),

    USER_GROUP_CREATE_TOO_FAST("A000211", "分组生成太快，请稍后再试");

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
