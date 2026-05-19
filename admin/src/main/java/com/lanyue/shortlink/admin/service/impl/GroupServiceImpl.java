package com.lanyue.shortlink.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanyue.shortlink.admin.common.biz.user.UserContext;
import com.lanyue.shortlink.admin.common.constant.RedisKeyConstant;
import com.lanyue.shortlink.admin.common.convention.errorcode.BaseErrorCode;
import com.lanyue.shortlink.admin.common.convention.exception.ServiceException;
import com.lanyue.shortlink.admin.common.convention.result.Result;
import com.lanyue.shortlink.admin.dao.entity.GroupDO;
import com.lanyue.shortlink.admin.dao.mapper.GroupMapper;
import com.lanyue.shortlink.admin.dto.req.GroupDeleteReqDTO;
import com.lanyue.shortlink.admin.dto.req.GroupUpdateReqDTO;
import com.lanyue.shortlink.admin.dto.resp.GroupRespDTO;
import com.lanyue.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.lanyue.shortlink.admin.remote.ShortLinkActualRemoteService;
import com.lanyue.shortlink.admin.service.GroupService;
import com.lanyue.shortlink.admin.tookit.RandomGeneration;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Value;
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

    private final ShortLinkActualRemoteService shortLinkActualRemoteService;

    private final Redisson redisson;

    @Value("${short-link.group.max-size}")
    private final int groupMaxNum;

    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        LambdaQueryWrapper<GroupDO> wrapper = new LambdaQueryWrapper<>(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groupDOS = baseMapper.selectList(wrapper);
        // TODO 获取分组下的短链接,需要远程调用
        return null;
    }

    @Override
    public void saveGroup(String username, String groupName) {
        RLock lock = redisson.getLock(String.format(RedisKeyConstant.LOCK_GROUP_CREATE_KEY, groupName));
        lock.lock();
        try {
            List<GroupRespDTO> groupRespDTOS = listGroup();
            if(groupRespDTOS != null && groupRespDTOS.size() >= groupMaxNum) {
                throw new ServiceException("已超出最大分组数" + groupMaxNum);
            }
            String gid = null;
            int retryCount = 0;
            int maxRetryCount = 10;
            while (retryCount < maxRetryCount) {
                gid = generateGid();
                if (StrUtil.isNotBlank(gid)) {
                    GroupDO groupDO = GroupDO.builder()
                            .gid(gid)
                            .username(username)
                            .name(groupName)
                            .build();
                    baseMapper.insert(groupDO);
                    userGroupBloomFilter.add(gid);
                    break;
                }
                retryCount++;
            }
            if (StrUtil.isBlank(gid)) {
                throw new ServiceException(BaseErrorCode.FLOW_LIMIT_ERROR);
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
