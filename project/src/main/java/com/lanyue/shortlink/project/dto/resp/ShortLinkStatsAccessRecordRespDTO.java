package com.lanyue.shortlink.project.dto.resp;

import lombok.Data;

import java.util.Date;

/**
 * 短链接访问记录返回参数
 */
@Data
public class ShortLinkStatsAccessRecordRespDTO {

    /**
     * 访客类型
     */
    private String uvType;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 访问网络
     */
    private String network;

    /**
     * 访问设备
     */
    private String device;

    /**
     * 地区
     */
    private String locale;

    /**
     * 用户信息
     */
    private String user;

    /**
     * 访问时间
     */
    private Date createTime;
}