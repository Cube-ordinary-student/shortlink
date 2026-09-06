package com.lanyue.shortlink.admin.remote.dto.req;

import lombok.Data;

/**
 * 短链接统计请求参数
 */
@Data
public class ShortLinkStatsReqDTO {

    private String fullShortUrl;

    private String gid;

    private Integer enableStatus;

    private String startDate;

    private String endDate;
}