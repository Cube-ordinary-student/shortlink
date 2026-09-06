package com.lanyue.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lanyue.shortlink.admin.common.convention.result.Result;
import com.lanyue.shortlink.admin.remote.ShortLinkStatsRemoteService;
import com.lanyue.shortlink.admin.remote.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.lanyue.shortlink.admin.remote.dto.req.ShortLinkStatsReqDTO;
import com.lanyue.shortlink.admin.remote.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.lanyue.shortlink.admin.remote.dto.resp.ShortLinkStatsRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接统计控制层
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkStatsController {

    private final ShortLinkStatsRemoteService shortLinkStatsRemoteService;

    @GetMapping("/api/short-link/admin/v1/stats")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO requestParam) {
        return shortLinkStatsRemoteService.shortLinkStats(requestParam);
    }

    @GetMapping("/api/short-link/admin/v1/stats/group")
    public Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        return shortLinkStatsRemoteService.groupShortLinkStats(requestParam);
    }

    @GetMapping("/api/short-link/admin/v1/stats/access-record")
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        return shortLinkStatsRemoteService.shortLinkStatsAccessRecord(requestParam);
    }

    @GetMapping("/api/short-link/admin/v1/stats/access-record/group")
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> groupShortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        return shortLinkStatsRemoteService.groupShortLinkStatsAccessRecord(requestParam);
    }
}