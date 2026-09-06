package com.lanyue.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lanyue.shortlink.admin.dao.entity.GroupDO;
import com.lanyue.shortlink.admin.dto.req.GroupUpdateReqDTO;
import com.lanyue.shortlink.admin.dto.req.ShortLinkGroupReqDTO;
import com.lanyue.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

public interface GroupService extends IService<GroupDO> {

    List<ShortLinkGroupRespDTO> listGroup();

    void saveGroup(String username, String groupName);

    void updateGroup(GroupUpdateReqDTO requestParam);

    void deleteGroup(String gid);

    void sortGroup(List<ShortLinkGroupReqDTO> requestParam);
}