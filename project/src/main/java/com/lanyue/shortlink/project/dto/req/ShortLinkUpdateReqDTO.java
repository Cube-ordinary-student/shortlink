package com.lanyue.shortlink.project.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShortLinkUpdateReqDTO {

    @NotNull(message = "短链接ID不能为空")
    private Long id;

    @NotBlank(message = "原始链接不能为空")
    private String originUrl;

    @NotBlank(message = "分组ID不能为空")
    private String gid;
}
