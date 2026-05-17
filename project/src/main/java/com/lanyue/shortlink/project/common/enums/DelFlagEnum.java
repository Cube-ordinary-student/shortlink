package com.lanyue.shortlink.project.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DelFlagEnum {

    NORMAL(0),

    DELETED(1);

    private final Integer code;
}
