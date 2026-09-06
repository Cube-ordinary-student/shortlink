package com.lanyue.shortlink.admin.remote.dto.resp;

import lombok.Data;

import java.util.Date;

/**
 * 短链接访问记录返回参数
 */
@Data
public class ShortLinkStatsAccessRecordRespDTO {

    private String uvType;

    private String browser;

    private String os;

    private String ip;

    private String network;

    private String device;

    private String locale;

    private String user;

    private Date createTime;
}