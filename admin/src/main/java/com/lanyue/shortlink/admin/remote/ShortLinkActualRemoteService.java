package com.lanyue.shortlink.admin.remote;

import com.lanyue.shortlink.admin.common.convention.result.Result;
import com.lanyue.shortlink.admin.dto.resp.ShortLinkGroupCountQueryRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "short-link-project", url = "${aggregation.remote-url:}", contextId = "shortLinkActualRemoteService")
public interface ShortLinkActualRemoteService {

    @GetMapping("/api/short-link/v1/count")
    Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(@RequestParam("requestParam") List<String> requestParam);

    @PostMapping("/api/short-link/v1/delete-by-gid")
    Result<Void> deleteByGid(@RequestParam("gid") String gid);
}
