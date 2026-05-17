package com.lanyue.shortlink.project.service.Impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanyue.shortlink.project.common.constant.CommonConstant;
import com.lanyue.shortlink.project.common.constant.RedisCacheConstant;
import com.lanyue.shortlink.project.common.convention.exception.ClientException;
import com.lanyue.shortlink.project.dao.entity.ShortLinkDO;
import com.lanyue.shortlink.project.dao.mapper.ShortLinkMapper;
import com.lanyue.shortlink.project.dto.req.*;
import com.lanyue.shortlink.project.dto.resp.ShortLinkRecycleRespDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkRespDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkStatsRespDTO;
import com.lanyue.shortlink.project.service.ShortLinkService;
import com.lanyue.shortlink.project.tookit.HashUtils;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortLinkBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public IPage<ShortLinkRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return null;
    }

    @Override
    public void saveShortLink(ShortLinkSaveReqDTO requestParam) {
        String customSuffix = requestParam.getCustomSuffix();
        String originUrl = requestParam.getOriginUrl();
        if(StrUtil.isBlank(customSuffix)) {
           //生成随机后缀
            int tryCount = 0;
            int maxTryCount = 10;
            while (tryCount < maxTryCount) {
                String uuid = UUID.randomUUID().toString();
                String url = originUrl + uuid;
                customSuffix = HashUtils.generateShortLinkSuffix(url);
                if (!hasSuffix(customSuffix))
                    break;
                tryCount++;
            }
            if (tryCount >= maxTryCount) {
                throw new ClientException("生成短链失败，请稍后再试");
            }
        }else  {
            // 校验自定义后缀是否已存在
            if (hasSuffix(customSuffix)) {
                throw new ClientException("自定义后缀已存在");
            }
        }
        RLock lock = redissonClient.getLock(RedisCacheConstant.SHORT_LINK_SUFFIX_KEY + customSuffix);
        lock.lock();
        try {
            ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                    .gid(requestParam.getGid())
                    .originUrl(originUrl)
                    .shortLinkSuffix(customSuffix)
                    .domain(CommonConstant.DOMAIN)
                    .build();
            baseMapper.insert(shortLinkDO);
            stringRedisTemplate.opsForValue().set(RedisCacheConstant.SHORT_LINK_KEY + customSuffix, originUrl,30, TimeUnit.MINUTES);
            shortLinkBloomFilter.add(customSuffix);
        }finally {
            lock.unlock();
        }

    }

    private boolean hasSuffix(String customSuffix) {
        return shortLinkBloomFilter.contains(customSuffix);
    }

    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
    }

    @Override
    public void moveToRecycleBin(ShortLinkRecycleReqDTO requestParam) {
    }

    @Override
    public IPage<ShortLinkRecycleRespDTO> pageRecycleBin(ShortLinkPageReqDTO requestParam) {
        return null;
    }

    @Override
    public void recoverFromRecycleBin(ShortLinkRecycleReqDTO requestParam) {
    }

    @Override
    public void removeFromRecycleBin(ShortLinkRecycleReqDTO requestParam) {
    }

    @Override
    public ShortLinkStatsRespDTO getStats(ShortLinkStatsReqDTO requestParam) {
        return null;
    }
}
