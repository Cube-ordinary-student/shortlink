package com.lanyue.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lanyue.shortlink.project.dao.entity.ShortLinkDO;
import com.lanyue.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.lanyue.shortlink.project.dto.req.ShortLinkRecycleReqDTO;
import com.lanyue.shortlink.project.dto.req.ShortLinkSaveReqDTO;
import com.lanyue.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.lanyue.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkRecycleRespDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkRespDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkStatsRespDTO;

public interface ShortLinkService extends IService<ShortLinkDO> {

    IPage<ShortLinkRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

    void saveShortLink(ShortLinkSaveReqDTO requestParam);

    void updateShortLink(ShortLinkUpdateReqDTO requestParam);

    void moveToRecycleBin(ShortLinkRecycleReqDTO requestParam);

    IPage<ShortLinkRecycleRespDTO> pageRecycleBin(ShortLinkPageReqDTO requestParam);

    void recoverFromRecycleBin(ShortLinkRecycleReqDTO requestParam);

    void removeFromRecycleBin(ShortLinkRecycleReqDTO requestParam);

    ShortLinkStatsRespDTO getStats(ShortLinkStatsReqDTO requestParam);
}
