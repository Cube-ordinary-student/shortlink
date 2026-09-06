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
import com.lanyue.shortlink.admin.dto.req.GroupUpdateReqDTO;
import com.lanyue.shortlink.admin.dto.req.ShortLinkGroupReqDTO;
import com.lanyue.shortlink.admin.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.lanyue.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.lanyue.shortlink.admin.remote.ShortLinkActualRemoteService;
import com.lanyue.shortlink.admin.service.GroupService;
import com.lanyue.shortlink.admin.tookit.RandomGeneration;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分组接口实现层
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    private final StringRedisTemplate stringRedisTemplate;

    private final RBloomFilter<String> userGroupBloomFilter;

    private final ShortLinkActualRemoteService shortLinkActualRemoteService;

    private final RedissonClient redissonClient;

    @Value("${short-link.group.max-size}")
    private int groupMaxNum;

    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        List<GroupDO> groupDOList = baseMapper.selectList(Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder)
                .orderByDesc(GroupDO::getUpdateTime));
        if (groupDOList == null || groupDOList.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> gidList = groupDOList.stream().map(GroupDO::getGid).collect(Collectors.toList());
        Result<List<ShortLinkGroupCountQueryRespDTO>> countResult = shortLinkActualRemoteService.listGroupShortLinkCount(gidList);
        List<ShortLinkGroupCountQueryRespDTO> countQueryRespDTOList = countResult.getData();

        return groupDOList.stream().map(each -> {
            ShortLinkGroupRespDTO shortLinkGroupRespDTO = ShortLinkGroupRespDTO.builder()
                    .gid(each.getGid())
                    .name(each.getName())
                    .sortOrder(each.getSortOrder())
                    .shortLinkCount(0)
                    .build();
            if (countQueryRespDTOList != null) {
                countQueryRespDTOList.stream()
                        .filter(countDTO -> each.getGid().equals(countDTO.getGid()))
                        .findFirst()
                        .ifPresent(countDTO -> shortLinkGroupRespDTO.setShortLinkCount(countDTO.getCount()));
            }
            return shortLinkGroupRespDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public void saveGroup(String username, String groupName) {
        RLock lock = redissonClient.getLock(String.format(RedisKeyConstant.LOCK_GROUP_CREATE_KEY, groupName));
        lock.lock();
        try {
            List<GroupDO> groupDOS = baseMapper.selectList(new LambdaQueryWrapper<GroupDO>()
                    .eq(GroupDO::getUsername, username)
                    .eq(GroupDO::getDelFlag, 0));
            if(groupDOS != null && groupDOS.size() >= groupMaxNum) {
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
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setName(requestParam.getName());
        baseMapper.update(groupDO, updateWrapper);
    }

    @Override
    public void deleteGroup(String gid) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        baseMapper.update(groupDO, updateWrapper);
        // 级联删除分组下的短链接
        shortLinkActualRemoteService.deleteByGid(gid);
    }

    @Override
    public void sortGroup(List<ShortLinkGroupReqDTO> requestParam) {
        requestParam.forEach(each -> {
            GroupDO groupDO = GroupDO.builder()
                    .sortOrder(each.getSortOrder())
                    .build();
            LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                    .eq(GroupDO::getUsername, UserContext.getUsername())
                    .eq(GroupDO::getGid, each.getGid())
                    .eq(GroupDO::getDelFlag, 0);
            baseMapper.update(groupDO, updateWrapper);
        });
    }
}
