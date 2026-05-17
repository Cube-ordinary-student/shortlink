package com.lanyue.shortlink.project.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RBloomFilterConfiguration {
    @Bean
    public RBloomFilter<String> shortlinkBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("shortlinkBloomFilter");
        bloomFilter.tryInit(100000000L, 0.001);
        return bloomFilter;
    }
}
