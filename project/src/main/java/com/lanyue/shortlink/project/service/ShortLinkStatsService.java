package com.lanyue.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lanyue.shortlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.lanyue.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkStatsRespDTO;

/**
 * 短链接统计服务接口
 */
public interface ShortLinkStatsService {

    /**
     * 访问单个短链接指定时间内监控数据
     */
    ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam);

    /**
     * 访问分组短链接指定时间内监控数据
     */
    ShortLinkStatsRespDTO groupShortLinkStats(ShortLinkStatsReqDTO requestParam);

    /**
     * 访问单个短链接指定时间内访问记录监控数据
     */
    IPage<ShortLinkStatsAccessRecordRespDTO> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam);

    /**
     * 访问分组短链接指定时间内访问记录监控数据
     */
    IPage<ShortLinkStatsAccessRecordRespDTO> groupShortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam);
}