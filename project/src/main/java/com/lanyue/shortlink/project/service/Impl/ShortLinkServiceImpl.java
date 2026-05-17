package com.lanyue.shortlink.project.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanyue.shortlink.project.dao.entity.ShortLinkDO;
import com.lanyue.shortlink.project.dao.mapper.ShortLinkMapper;
import com.lanyue.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.lanyue.shortlink.project.dto.req.ShortLinkRecycleReqDTO;
import com.lanyue.shortlink.project.dto.req.ShortLinkSaveReqDTO;
import com.lanyue.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.lanyue.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkRecycleRespDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkRespDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkStatsRespDTO;
import com.lanyue.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    @Override
    public IPage<ShortLinkRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return null;
    }

    @Override
    public void saveShortLink(ShortLinkSaveReqDTO requestParam) {

    }

    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
    }

    @Override
    public void moveToRecycleBin(ShortLinkRecycleReqDTO requestParam) {
    }

    @Override
    public IPage<ShortLinkRecycleRespDTO> pageRecycleBin(ShortLinkPageReqDTO requestParam) {
        return null;
    }

    @Override
    public void recoverFromRecycleBin(ShortLinkRecycleReqDTO requestParam) {
    }

    @Override
    public void removeFromRecycleBin(ShortLinkRecycleReqDTO requestParam) {
    }

    @Override
    public ShortLinkStatsRespDTO getStats(ShortLinkStatsReqDTO requestParam) {
        return null;
    }
}
