package com.lanyue.shortlink.admin.remote;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lanyue.shortlink.admin.common.convention.result.Result;
import com.lanyue.shortlink.admin.remote.dto.req.*;
import com.lanyue.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.lanyue.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "short-link-project", url = "${aggregation.remote-url:}", contextId = "shortLinkRemoteService")
public interface ShortLinkRemoteService {

    @PostMapping("/api/short-link/v1/create")
    Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam);

    @GetMapping("/api/short-link/v1/page")
    Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam);

    @PostMapping("/api/short-link/v1/update")
    Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam);

    @PostMapping("/api/short-link/v1/recycle-bin/save")
    Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam);

    @GetMapping("/api/short-link/v1/recycle-bin/page")
    Result<IPage<ShortLinkPageRespDTO>> pageRecycleBin(ShortLinkPageReqDTO requestParam);

    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam);

    @PostMapping("/api/short-link/v1/recycle-bin/remove")
    Result<Void> removeRecycleBin(@RequestBody RecycleBinRemoveReqDTO requestParam);

    @GetMapping("/api/short-link/v1/title")
    Result<String> getTitleByUrl(@RequestParam("url") String url);
}