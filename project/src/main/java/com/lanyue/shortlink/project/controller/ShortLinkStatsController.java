package com.lanyue.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lanyue.shortlink.project.common.convention.result.Result;
import com.lanyue.shortlink.project.common.convention.result.Results;
import com.lanyue.shortlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.lanyue.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkStatsRespDTO;
import com.lanyue.shortlink.project.service.ShortLinkStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接统计控制层
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkStatsController {

    private final ShortLinkStatsService shortLinkStatsService;

    /**
     * 访问单个短链接指定时间内监控数据
     */
    @GetMapping("/api/short-link/v1/stats")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO requestParam) {
        ShortLinkStatsRespDTO result = shortLinkStatsService.oneShortLinkStats(requestParam);
        return Results.success(result);
    }

    /**
     * 访问分组短链接指定时间内监控数据
     */
    @GetMapping("/api/short-link/v1/stats/group")
    public Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        ShortLinkStatsRespDTO result = shortLinkStatsService.groupShortLinkStats(requestParam);
        return Results.success(result);
    }

    /**
     * 访问单个短链接指定时间内访问记录监控数据
     */
    @GetMapping("/api/short-link/v1/stats/access-record")
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        IPage<ShortLinkStatsAccessRecordRespDTO> result = shortLinkStatsService.shortLinkStatsAccessRecord(requestParam);
        return Results.success(result);
    }

    /**
     * 访问分组短链接指定时间内访问记录监控数据
     */
    @GetMapping("/api/short-link/v1/stats/access-record/group")
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> groupShortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        IPage<ShortLinkStatsAccessRecordRespDTO> result = shortLinkStatsService.groupShortLinkStatsAccessRecord(requestParam);
        return Results.success(result);
    }
}