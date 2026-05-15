package com.lanyue.shortlink.admin.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录响应参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRespDTO {

    private String token;

}
