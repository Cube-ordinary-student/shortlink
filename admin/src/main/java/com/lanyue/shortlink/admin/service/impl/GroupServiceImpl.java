package com.lanyue.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanyue.shortlink.admin.dao.entity.GroupDO;
import com.lanyue.shortlink.admin.dao.mapper.GroupMapper;
import com.lanyue.shortlink.admin.dto.req.GroupDeleteReqDTO;
import com.lanyue.shortlink.admin.dto.req.GroupSaveReqDTO;
import com.lanyue.shortlink.admin.dto.req.GroupUpdateReqDTO;
import com.lanyue.shortlink.admin.dto.resp.GroupRespDTO;
import com.lanyue.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分组接口实现层
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    @Override
    public List<GroupRespDTO> listGroup() {
        return List.of();
    }

    @Override
    public void saveGroup(GroupSaveReqDTO requestParam) {

    }

    @Override
    public void updateGroup(GroupUpdateReqDTO requestParam) {

    }

    @Override
    public void deleteGroup(GroupDeleteReqDTO requestParam) {

    }
}
