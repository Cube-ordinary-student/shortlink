package com.lanyue.shortlink.admin.remote;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lanyue.shortlink.admin.common.convention.result.Result;
import com.lanyue.shortlink.admin.remote.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.lanyue.shortlink.admin.remote.dto.req.ShortLinkStatsReqDTO;
import com.lanyue.shortlink.admin.remote.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.lanyue.shortlink.admin.remote.dto.resp.ShortLinkStatsRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 短链接统计远程调用服务
 */
@FeignClient(value = "short-link-project", url = "${aggregation.remote-url:}", contextId = "shortLinkStatsRemoteService")
public interface ShortLinkStatsRemoteService {

    @GetMapping("/api/short-link/v1/stats")
    Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO requestParam);

    @GetMapping("/api/short-link/v1/stats/group")
    Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkStatsReqDTO requestParam);

    @GetMapping("/api/short-link/v1/stats/access-record")
    Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam);

    @GetMapping("/api/short-link/v1/stats/access-record/group")
    Result<IPage<ShortLinkStatsAccessRecordRespDTO>> groupShortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam);
}