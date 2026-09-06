package com.lanyue.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lanyue.shortlink.project.common.convention.result.Result;
import com.lanyue.shortlink.project.common.convention.result.Results;
import com.lanyue.shortlink.project.dto.req.RecycleBinRecoverReqDTO;
import com.lanyue.shortlink.project.dto.req.RecycleBinRemoveReqDTO;
import com.lanyue.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.lanyue.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.lanyue.shortlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    private final RecycleBinService recycleBinService;

    @PostMapping("/api/short-link/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam) {
        recycleBinService.saveRecycleBin(requestParam);
        return Results.success();
    }

    @GetMapping("/api/short-link/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleBin(ShortLinkPageReqDTO requestParam) {
        IPage<ShortLinkPageRespDTO> result = recycleBinService.pageRecycleBin(requestParam);
        return Results.success(result);
    }

    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    public Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam) {
        recycleBinService.recoverRecycleBin(requestParam);
        return Results.success();
    }

    @PostMapping("/api/short-link/v1/recycle-bin/remove")
    public Result<Void> removeRecycleBin(@RequestBody RecycleBinRemoveReqDTO requestParam) {
        recycleBinService.removeRecycleBin(requestParam);
        return Results.success();
    }
}