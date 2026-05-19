package com.lanyue.shortlink.admin.controller;

import com.lanyue.shortlink.admin.common.biz.user.UserContext;
import com.lanyue.shortlink.admin.common.convention.result.Result;
import com.lanyue.shortlink.admin.common.convention.result.Results;
import com.lanyue.shortlink.admin.dto.req.GroupDeleteReqDTO;
import com.lanyue.shortlink.admin.dto.req.GroupSaveReqDTO;
import com.lanyue.shortlink.admin.dto.req.GroupUpdateReqDTO;
import com.lanyue.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.lanyue.shortlink.admin.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分组管理控制层
 */
@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    /**
     * 查询分组列表
     */
    @GetMapping("/api/short-link/admin/v1/group")
    public Result<List<ShortLinkGroupRespDTO>> listGroup() {
        List<ShortLinkGroupRespDTO> result = groupService.listGroup();
        return Results.success(result);
    }

    /**
     * 创建分组
     */
    @PostMapping("/api/short-link/admin/v1/group/create")
    public Result<Void> saveGroup(@RequestBody @Valid GroupSaveReqDTO requestParam) {
        groupService.saveGroup(UserContext.getUsername(),requestParam.getName());
        return Results.success("创建成功", null);
    }

    /**
     * 更新分组
     */
    @PostMapping("/api/short-link/admin/v1/group/update")
    public Result<Void> updateGroup(@RequestBody @Valid GroupUpdateReqDTO requestParam) {
        groupService.updateGroup(requestParam);
        return Results.success("更新成功", null);
    }

    /**
     * 删除分组
     */
    @PostMapping("/api/short-link/admin/v1/group/delete")
    public Result<Void> deleteGroup(@RequestBody @Valid GroupDeleteReqDTO requestParam) {
        groupService.deleteGroup(requestParam);
        return Results.success("删除成功", null);
    }
}
