package com.lanyue.shortlink.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GatewayErrorResult {
    /**
     * HTTP 状态码
     */
    private Integer status;
    /**
     * 返回信息
     */
    private String message;
}
