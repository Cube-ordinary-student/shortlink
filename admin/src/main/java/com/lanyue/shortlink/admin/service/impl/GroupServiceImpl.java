package com.lanyue.shortlink.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanyue.shortlink.admin.common.biz.user.UserContext;
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
        return List.of();
    }

    @Override
    public void saveGroup(GroupSaveReqDTO requestParam) {
        String name = requestParam.getName();
        RLock lock = redisson.getLock(String.format(RedisCacheConstant.USER_GROUP_KEY, name));
        lock.lock();
        try {
            //TODO :出数据库中查询是否存在，用户的所有分组，是否达到分组数量的限制


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
