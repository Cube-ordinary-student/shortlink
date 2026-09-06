package com.lanyue.shortlink.project.dto.resp;

import lombok.Data;

import java.util.List;

/**
 * 短链接统计返回参数
 */
@Data
public class ShortLinkStatsRespDTO {

    /**
     * 访问量
     */
    private Integer pv;

    /**
     * 独立访客数
     */
    private Integer uv;

    /**
     * 独立IP数
     */
    private Integer uip;

    /**
     * 小时访问详情（24小时）
     */
    private List<Integer> hourStats;

    /**
     * 一周访问详情（7天）
     */
    private List<Integer> weekdayStats;
}