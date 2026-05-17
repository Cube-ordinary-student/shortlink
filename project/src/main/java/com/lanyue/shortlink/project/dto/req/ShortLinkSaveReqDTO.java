package com.lanyue.shortlink.project.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShortLinkSaveReqDTO {

    @NotBlank(message = "原始链接不能为空")
    private String originUrl;

    @NotBlank(message = "分组ID不能为空")
    private String gid;

    private String customSuffix;
}
