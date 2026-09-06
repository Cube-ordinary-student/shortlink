package com.lanyue.shortlink.project.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lanyue.shortlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.lanyue.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkStatsRespDTO;
import com.lanyue.shortlink.project.service.ShortLinkStatsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 短链接统计服务实现
 */
@Service
public class ShortLinkStatsServiceImpl implements ShortLinkStatsService {

    @Override
    public ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        ShortLinkStatsRespDTO result = new ShortLinkStatsRespDTO();
        result.setPv(0);
        result.setUv(0);
        result.setUip(0);
        result.setHourStats(new ArrayList<>(Collections.nCopies(24, 0)));
        result.setWeekdayStats(new ArrayList<>(Collections.nCopies(7, 0)));
        return result;
    }

    @Override
    public ShortLinkStatsRespDTO groupShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        return oneShortLinkStats(requestParam);
    }

    @Override
    public IPage<ShortLinkStatsAccessRecordRespDTO> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        return new Page<>(requestParam.getCurrent(), requestParam.getSize());
    }

    @Override
    public IPage<ShortLinkStatsAccessRecordRespDTO> groupShortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        return new Page<>(requestParam.getCurrent(), requestParam.getSize());
    }
}