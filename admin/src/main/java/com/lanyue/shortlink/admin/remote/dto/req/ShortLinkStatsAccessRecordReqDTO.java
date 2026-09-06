package com.lanyue.shortlink.admin.remote.dto.req;

import lombok.Data;

/**
 * 短链接访问记录请求参数
 */
@Data
public class ShortLinkStatsAccessRecordReqDTO {

    private String fullShortUrl;

    private String gid;

    private String startDate;

    private String endDate;

    private Integer enableStatus;

    private Long current;

    private Long size;
}