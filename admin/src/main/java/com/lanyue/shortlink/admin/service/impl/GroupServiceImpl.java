package com.lanyue.shortlink.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanyue.shortlink.admin.common.biz.user.UserContext;
import com.lanyue.shortlink.admin.common.constant.CommonConstant;
import com.lanyue.shortlink.admin.common.constant.RedisCacheConstant;
import com.lanyue.shortlink.admin.common.convention.exception.ServiceException;
import com.lanyue.shortlink.admin.common.enums.GroupErrorCodeEnum;
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
        QueryWrapper<GroupDO> queryWrapper = new QueryWrapper<GroupDO>()
                .eq(CommonConstant.USERNAME, UserContext.getUsername());
        List<GroupDO> groupDOS = baseMapper.selectList(queryWrapper);
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

    }

    @Override
    public void deleteGroup(GroupDeleteReqDTO requestParam) {

    }
}
