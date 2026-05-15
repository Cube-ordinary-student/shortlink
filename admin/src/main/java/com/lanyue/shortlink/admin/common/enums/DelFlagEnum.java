package com.lanyue.shortlink.admin.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 删除标记枚举
 */
@Getter
@RequiredArgsConstructor
public enum DelFlagEnum {

    NOT_DELETE(0, "未删除"),

    DELETE(1, "已删除");

    private final Integer code;

    private final String desc;

    public static DelFlagEnum fromCode(Integer code) {
        for (DelFlagEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return NOT_DELETE;
    }
}
