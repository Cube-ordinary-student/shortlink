package com.lanyue.shortlink.admin.remote.dto.resp;

import lombok.Data;

import java.util.List;

/**
 * 短链接统计返回参数
 */
@Data
public class ShortLinkStatsRespDTO {

    private Integer pv;

    private Integer uv;

    private Integer uip;

    private List<Integer> hourStats;

    private List<Integer> weekdayStats;
}