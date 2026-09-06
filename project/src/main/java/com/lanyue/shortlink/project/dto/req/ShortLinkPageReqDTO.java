package com.lanyue.shortlink.project.dto.req;

import lombok.Data;

import java.util.List;

@Data
public class ShortLinkPageReqDTO {
    /**
     * 分组标识
     */
    private String gid;
    /**
     * 分组标识集合（回收站分页查询使用）
     */
    private List<String> gidList;
    /**
     * 排序类型
     */
    private String orderTag;
    /**
     * 当前页码
     */
    private Long current;
    /**
     * 每页数量
     */
    private Long size;
}
