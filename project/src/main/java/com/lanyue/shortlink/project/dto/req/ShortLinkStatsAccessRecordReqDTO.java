package com.lanyue.shortlink.project.dto.req;

import lombok.Data;

/**
 * 短链接访问记录请求参数
 */
@Data
public class ShortLinkStatsAccessRecordReqDTO {

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 启用标识
     */
    private Integer enableStatus;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页数量
     */
    private Long size;
}