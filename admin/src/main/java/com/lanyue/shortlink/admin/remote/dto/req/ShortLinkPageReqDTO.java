package com.lanyue.shortlink.admin.remote.dto.req;

import lombok.Data;

import java.util.List;

@Data
public class ShortLinkPageReqDTO {

    private String gid;

    /**
     * 分组标识集合（回收站分页查询使用）
     */
    private List<String> gidList;

    private String orderTag;

    private Long current;

    private Long size;
}