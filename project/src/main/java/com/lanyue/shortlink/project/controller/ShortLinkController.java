package com.lanyue.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lanyue.shortlink.project.dto.req.*;
import com.lanyue.shortlink.project.dto.resp.ShortLinkRecycleRespDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkRespDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkStatsRespDTO;
import com.lanyue.shortlink.project.service.ShortLinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    @GetMapping("/api/short-link/admin/v1/page")
    public IPage<ShortLinkRespDTO> pageShortLink(@Valid ShortLinkPageReqDTO requestParam) {
        return shortLinkService.pageShortLink(requestParam);
    }

    @PostMapping("/api/short-link/admin/v1/create")
    public void saveShortLink(@RequestBody @Valid ShortLinkSaveReqDTO requestParam) {
        shortLinkService.saveShortLink(requestParam);
    }

    @PostMapping("/api/short-link/admin/v1/update")
    public void updateShortLink(@RequestBody @Valid ShortLinkUpdateReqDTO requestParam) {
        shortLinkService.updateShortLink(requestParam);
    }

    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    public void moveToRecycleBin(@RequestBody @Valid ShortLinkRecycleReqDTO requestParam) {
        shortLinkService.moveToRecycleBin(requestParam);
    }

    @GetMapping("/api/short-link/admin/v1/recycle-bin/page")
    public IPage<ShortLinkRecycleRespDTO> pageRecycleBin(@Valid ShortLinkPageReqDTO requestParam) {
        return shortLinkService.pageRecycleBin(requestParam);
    }

    @PostMapping("/api/short-link/admin/v1/recycle-bin/recover")
    public void recoverFromRecycleBin(@RequestBody @Valid ShortLinkRecycleReqDTO requestParam) {
        shortLinkService.recoverFromRecycleBin(requestParam);
    }

    @PostMapping("/api/short-link/admin/v1/recycle-bin/remove")
    public void removeFromRecycleBin(@RequestBody @Valid ShortLinkRecycleReqDTO requestParam) {
        shortLinkService.removeFromRecycleBin(requestParam);
    }

    @GetMapping("/api/short-link/admin/v1/stats")
    public ShortLinkStatsRespDTO getStats(@RequestParam("id") Long id) {
        ShortLinkStatsReqDTO requestParam = new ShortLinkStatsReqDTO();
        requestParam.setId(id);
        return shortLinkService.getStats(requestParam);
    }
}
