package com.lanyue.shortlink.project.service.Impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.text.StrBuilder;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanyue.shortlink.admin.common.convention.exception.ClientException;
import com.lanyue.shortlink.admin.common.convention.exception.ServiceException;
import com.lanyue.shortlink.project.dao.entity.ShortLinkDO;
import com.lanyue.shortlink.project.dao.entity.ShortLinkGotoDO;
import com.lanyue.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import com.lanyue.shortlink.project.dao.mapper.ShortLinkMapper;
import com.lanyue.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.lanyue.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.lanyue.shortlink.project.service.ShortLinkService;
import com.lanyue.shortlink.project.tookit.HashUtils;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortLinkBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final JvmThreadMetrics jvmThreadMetrics;

    @Value("${short-link.domain.default}")
    private String createShortLinkDefaultDomain;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String originUrl = requestParam.getOriginUrl();
        String suffix = generateShortlinkSuffix(originUrl);
        String fullShortUrl = StrBuilder.create(createShortLinkDefaultDomain)
                .append("/")
                .append(suffix)
                .toString();
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(createShortLinkDefaultDomain)
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .createdType(requestParam.getCreatedType())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .describe(requestParam.getDescribe())
                .shortUri(suffix)
                .enableStatus(0)
                .totalPv(0)
                .totalUv(0)
                .totalUip(0)
                .delTime(0L)
                .fullShortUrl(fullShortUrl)
                .favicon(getFavicon(requestParam.getOriginUrl()))
                .build();
        ShortLinkGotoDO gotoDO = ShortLinkGotoDO.builder()
                .gid(requestParam.getGid())
                .fullShortUrl(fullShortUrl)
                .build();
        try {
            baseMapper.insert(shortLinkDO);
            shortLinkGotoMapper.insert(gotoDO);
        } catch (DuplicateKeyException ex) {
            if (!shortLinkBloomFilter.contains(fullShortUrl)) {
                shortLinkBloomFilter.add(fullShortUrl);
            }
            throw new ServiceException(String.format("短链接: %s重复生成", fullShortUrl));
        }
        stringRedisTemplate.opsForValue().set(fullShortUrl, originUrl,1 ,TimeUnit.HOURS);
        shortLinkBloomFilter.add(fullShortUrl);
        return ShortLinkCreateRespDTO.builder()
                .gid(shortLinkDO.getGid())
                .originUrl(requestParam.getOriginUrl())
                .fullShortUrl("http://"+fullShortUrl)
                .build();
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        String gid = requestParam.getGid();
        if (gid == null) {
            throw new ClientException("gid不能为空");
        }

        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, gid)
                .eq(ShortLinkDO::getEnableStatus, 0)
                .eq(ShortLinkDO::getDelFlag, 0)
                .orderByDesc(ShortLinkDO::getCreateTime);

        IPage<ShortLinkDO> resultPage = baseMapper.selectPage(new Page<>(requestParam.getCurrent(), requestParam.getSize()), queryWrapper);
        
        return resultPage.convert(each -> {
            ShortLinkPageRespDTO result = cn.hutool.core.bean.BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
            result.setDomain("http://" + result.getDomain());
            return result;
        });
    }

    @Override
    public List<ShortLinkGroupCountQueryRespDTO> queryShortLinkGroupCount(List<String> requestParam) {
        if (requestParam == null || requestParam.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        QueryWrapper<ShortLinkDO> queryWrapper = Wrappers.query();
        queryWrapper.select("gid as gid, count(*) as count")
                .in("gid", requestParam)
                .eq("enable_status", 0)
                .eq("del_flag", 0)
                .groupBy("gid");

        List<java.util.Map<String, Object>> mapList = baseMapper.selectMaps(queryWrapper);

        return mapList.stream()
                .map(map -> ShortLinkGroupCountQueryRespDTO.builder()
                        .gid((String) map.get("gid"))
                        .count(java.util.Optional.ofNullable(map.get("count"))
                                .map(v -> ((Number) v).intValue())
                                .orElse(0))
                        .build())
                .toList();
    }

    @SneakyThrows
    private String getFavicon(String url) {
        URL targetUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (HttpURLConnection.HTTP_OK == responseCode) {
            Document document = Jsoup.connect(url).get();
            Element faviconLink = document.select("link[rel~=(?i)^(shortcut )?icon]").first();
            if (faviconLink != null) {
                return faviconLink.attr("abs:href");
            }
        }
        return null;
    }

    private String generateShortlinkSuffix(String originUrl) {
        int tryCount = 0;
        int maxCount = 10;
        while (tryCount < maxCount) {
            String url = originUrl + UUID.randomUUID();
            String suffix = HashUtils.generateShortLinkSuffix(url);
            if (!shortLinkBloomFilter.contains(createShortLinkDefaultDomain + "/" + suffix)) {
                return suffix;
            }
            tryCount++;
        }
        throw new ServiceException("生成短链接失败，请稍后再试");
    }
}
