package com.lanyue.shortlink.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.lanyue.shortlink.gateway.config.Config;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TokenValidateGatewayFilterFactory extends AbstractGatewayFilterFactory<Config> {

    private final StringRedisTemplate stringRedisTemplate;

    public TokenValidateGatewayFilterFactory(StringRedisTemplate stringRedisTemplate) {
        super(Config.class);
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            //检查请求路径
            ServerHttpRequest request = exchange.getRequest();
            String uri = request.getPath().toString();
            List<String> whitePathList = config.getWhitePathList();
            if (isWhitePath(uri, whitePathList)) {
                return chain.filter(exchange);
            }
            //获取用户token和姓名
            String token = request.getHeaders().getFirst("Authorization");
            String username = request.getHeaders().getFirst("username");
            if (StrUtil.isBlank(token)) {
                //强制用户登录
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            //TODO:去缓存中查询用户信息，验证token是否有效,缓存中不存在则返回用户不存在
            String userInfo = stringRedisTemplate.opsForValue().get("short-link:login:" + username);

            return chain.filter(exchange);
        };
    }

    private boolean isWhitePath(String uri, List<String> whitePathList) {
        return whitePathList.stream().anyMatch(uri::startsWith);
    }
}
