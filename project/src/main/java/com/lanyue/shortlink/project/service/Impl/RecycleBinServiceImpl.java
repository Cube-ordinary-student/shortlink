package com.lanyue.shortlink.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lanyue.shortlink.project.common.convention.exception.ClientException;
import com.lanyue.shortlink.project.dao.entity.ShortLinkDO;
import com.lanyue.shortlink.project.dao.entity.ShortLinkGotoDO;
import com.lanyue.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import com.lanyue.shortlink.project.dao.mapper.ShortLinkMapper;
import com.lanyue.shortlink.project.dto.req.RecycleBinRecoverReqDTO;
import com.lanyue.shortlink.project.dto.req.RecycleBinRemoveReqDTO;
import com.lanyue.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.lanyue.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.lanyue.shortlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {

    private final ShortLinkMapper shortLinkMapper;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRecycleBin(RecycleBinSaveReqDTO requestParam) {
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0)
                .set(ShortLinkDO::getDelFlag, 1)
                .set(ShortLinkDO::getDelTime, System.currentTimeMillis());
        int updated = shortLinkMapper.update(null, updateWrapper);
        if (updated < 1) {
            throw new ClientException("短链接不存在或已被删除");
        }
        stringRedisTemplate.delete(requestParam.getFullShortUrl());
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageRecycleBin(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getDelFlag, 1)
                .in(ShortLinkDO::getGid, requestParam.getGidList())
                .orderByDesc(ShortLinkDO::getUpdateTime);

        IPage<ShortLinkDO> resultPage = shortLinkMapper.selectPage(
                new Page<>(requestParam.getCurrent(), requestParam.getSize()), queryWrapper);

        return resultPage.convert(each -> {
            ShortLinkPageRespDTO result = cn.hutool.core.bean.BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
            result.setDomain("http://" + result.getDomain());
            return result;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam) {
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 1)
                .set(ShortLinkDO::getDelFlag, 0)
                .set(ShortLinkDO::getDelTime, 0L);
        int updated = shortLinkMapper.update(null, updateWrapper);
        if (updated < 1) {
            throw new ClientException("回收站短链接不存在");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRecycleBin(RecycleBinRemoveReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 1);
        ShortLinkDO shortLinkDO = shortLinkMapper.selectOne(queryWrapper);
        if (shortLinkDO == null) {
            throw new ClientException("回收站短链接不存在");
        }
        shortLinkMapper.deleteById(shortLinkDO.getId());
        LambdaQueryWrapper<ShortLinkGotoDO> gotoQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                .eq(ShortLinkGotoDO::getFullShortUrl, requestParam.getFullShortUrl());
        shortLinkGotoMapper.delete(gotoQueryWrapper);
    }
}