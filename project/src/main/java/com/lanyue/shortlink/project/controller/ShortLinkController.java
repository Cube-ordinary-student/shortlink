package com.lanyue.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lanyue.shortlink.project.common.convention.result.Result;
import com.lanyue.shortlink.project.common.convention.result.Results;
import com.lanyue.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.lanyue.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.lanyue.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    /**
     * 创建短链接
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        ShortLinkCreateRespDTO result = shortLinkService.createShortLink(requestParam);
        return Results.success(result);
    }

    /**
     * 分页查询短链接
     */
    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        IPage<ShortLinkPageRespDTO> result = shortLinkService.pageShortLink(requestParam);
        return Results.success(result);
    }

    /**
     * 查询分组短链接数量
     */
    @GetMapping("/api/short-link/v1/count")
    public Result<List<ShortLinkGroupCountQueryRespDTO>> queryShortLinkGroupCount(@RequestParam List<String> requestParam) {
        List<ShortLinkGroupCountQueryRespDTO> result = shortLinkService.queryShortLinkGroupCount(requestParam);
        return Results.success(result);
    }
}
