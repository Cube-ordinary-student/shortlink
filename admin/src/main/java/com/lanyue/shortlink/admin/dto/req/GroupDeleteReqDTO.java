package com.lanyue.shortlink.admin.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 分组删除请求参数
 */
@Data
public class GroupDeleteReqDTO {

    @NotNull(message = "分组ID不能为空")
    private Long id;
}
