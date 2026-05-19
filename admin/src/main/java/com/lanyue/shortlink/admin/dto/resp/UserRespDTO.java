package com.lanyue.shortlink.admin.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息响应参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRespDTO {
    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    @JsonSerialize(using = com.lanyue.shortlink.admin.common.serialize.RealNameDesensitizationSerializer.class)
    private String realName;

    /**
     * 手机号
     */
    @JsonSerialize(using = com.lanyue.shortlink.admin.common.serialize.PhoneDesensitizationSerializer.class)
    private String phone;

    /**
     * 邮箱
     */
    private String mail;
}
