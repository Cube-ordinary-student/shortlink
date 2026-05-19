package com.lanyue.shortlink.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanyue.shortlink.admin.common.biz.user.UserContext;
import com.lanyue.shortlink.admin.common.convention.exception.ServiceException;
import com.lanyue.shortlink.admin.dao.entity.GroupDO;
import com.lanyue.shortlink.admin.dao.mapper.GroupMapper;
import com.lanyue.shortlink.admin.dto.req.GroupDeleteReqDTO;
import com.lanyue.shortlink.admin.dto.req.GroupSaveReqDTO;
import com.lanyue.shortlink.admin.dto.req.GroupUpdateReqDTO;
import com.lanyue.shortlink.admin.dto.resp.GroupRespDTO;
import com.lanyue.shortlink.admin.service.GroupService;
import com.lanyue.shortlink.admin.tookit.RandomGeneration;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分组接口实现层
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    private final StringRedisTemplate stringRedisTemplate;

    private final RBloomFilter<String> userGroupBloomFilter;

    private final Redisson redisson;

    @Override
    public List<GroupRespDTO> listGroup() {
        LambdaQueryWrapper<GroupDO> wrapper = new LambdaQueryWrapper<>(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groupDOS = baseMapper.selectList(wrapper);
        return groupDOS.stream().map(groupDO -> GroupRespDTO.builder()
                .gid(groupDO.getGid())
                .name(groupDO.getName())
                .username(groupDO.getUsername())
                .description(groupDO.getDescription())
                .sortOrder(groupDO.getSortOrder())
                .createTime(groupDO.getCreateTime())
                .updateTime(groupDO.getUpdateTime())
                .build()).toList();
    }

    @Override
    public void saveGroup(GroupSaveReqDTO requestParam) {
        String name = requestParam.getName();
        RLock lock = redisson.getLock(String.format(RedisCacheConstant.USER_GROUP_KEY, name));
        lock.lock();
        try {
            List<GroupRespDTO> groupRespDTOS = listGroup();
            if(groupRespDTOS != null && groupRespDTOS.size() >= CommonConstant.GROUP_COUNT_MAX) {
                throw new ServiceException(GroupErrorCodeEnum.USER_GROUP_COUNT_MAX);
            }
            String gid = null;
            int retryCount = 0;
            int maxRetryCount = 10;
            while (retryCount < maxRetryCount) {
                gid = generateGid();
                if (StrUtil.isNotBlank(gid)) {
                    GroupDO groupDO = GroupDO.builder()
                            .gid(gid)
                            .username(UserContext.getUsername())
                            .name(name)
                            .build();
                    baseMapper.insert(groupDO);
                    userGroupBloomFilter.add(gid);
                    break;
                }
                retryCount++;
            }
            if (StrUtil.isBlank(gid)) {
                throw new ServiceException(GroupErrorCodeEnum.USER_GROUP_CREATE_TOO_FAST);
            }
            userGroupBloomFilter.add(gid);
        }finally {
            lock.unlock();
        }

    }

    private String generateGid() {
        String gid = RandomGeneration.generateRandom();
        if (userGroupBloomFilter.contains(gid)) {
            return null;
        }
        return gid;
    }

    @Override
    public void updateGroup(GroupUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, requestParam.getId())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setName(requestParam.getName());
        baseMapper.update(groupDO, updateWrapper);
    }

    @Override
    public void deleteGroup(GroupDeleteReqDTO requestParam) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, requestParam.getId())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        baseMapper.update(groupDO, updateWrapper);
        //TODO 分组下的短链接删除


    }
}
