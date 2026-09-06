package com.lanyue.shortlink.admin.dto.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortLinkGroupCountQueryRespDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组数量
     */
    private Integer count;
}
