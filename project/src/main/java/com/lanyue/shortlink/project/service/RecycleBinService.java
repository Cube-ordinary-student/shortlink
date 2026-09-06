package com.lanyue.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lanyue.shortlink.project.dto.req.RecycleBinRecoverReqDTO;
import com.lanyue.shortlink.project.dto.req.RecycleBinRemoveReqDTO;
import com.lanyue.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.lanyue.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkPageRespDTO;

public interface RecycleBinService {

    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);

    IPage<ShortLinkPageRespDTO> pageRecycleBin(ShortLinkPageReqDTO requestParam);

    void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam);

    void removeRecycleBin(RecycleBinRemoveReqDTO requestParam);
}