package com.lanyue.shortlink.admin.remote.dto.resp;

import lombok.Data;

import java.util.Date;

@Data
public class ShortLinkPageRespDTO {

    private Long id;

    private String domain;

    private String shortUri;

    private String fullShortUrl;

    private String originUrl;

    private String gid;

    private Integer validDateType;

    private Integer enableStatus;

    private Date validDate;

    private Date createTime;

    private String describe;

    private String favicon;

    private Integer totalPv;

    private Integer todayPv;

    private Integer totalUv;

    private Integer todayUv;

    private Integer totalUip;

    private Integer todayUip;
}