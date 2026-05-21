package com.lanyue.shortlink.admin.dto.req;

import lombok.Data;

@Data
public class ShortLinkGroupReqDTO {
    /**
     * 分组id
     */
    private String gid;
    /**
     * 排序id
     */
    private int sortOrder;
}
