package com.lanyue.shortlink.project.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortLinkPageReqDTO {
    /**
     * 分组标识
     */
    private String gid;
    /**
     * 排序类型
     */
    private String orderTag;
    /**
     * 当前页码
     */
    @NotBlank(message = "当前页码不能为空")
    private Long current;
    /**
     * 每页数量
     */
    @NotBlank(message = "每页数量不能为空")
    private Long size;
}
