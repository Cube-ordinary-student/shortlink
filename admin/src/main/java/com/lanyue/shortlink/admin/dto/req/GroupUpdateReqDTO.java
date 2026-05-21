package com.lanyue.shortlink.admin.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 分组更新请求参数
 */
@Data
public class GroupUpdateReqDTO {

    @NotNull(message = "分组ID不能为空")
    private String gid;

    @NotBlank(message = "分组名称不能为空")
    @Size(max = 100, message = "分组名称长度不能超过100个字符")
    private String name;
}
