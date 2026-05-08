package com.lanyue.shortlink.gateway.config;

import lombok.Data;

import java.util.List;

@Data
public class Config {
    /**
     * 白名单前置路径
     */
    private List<String> whitePathList;
}
