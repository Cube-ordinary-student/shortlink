package com.lanyue.shortlink.admin.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分组创建请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupSaveReqDTO {

    @NotBlank(message = "分组名称不能为空")
    @Size(max = 100, message = "分组名称长度不能超过100个字符")
    private String name;
}
