package com.lanyue.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanyue.shortlink.admin.common.constant.RedisCacheConstant;
import com.lanyue.shortlink.admin.common.convention.exception.ClientException;
import com.lanyue.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.lanyue.shortlink.admin.dao.entity.UserDO;
import com.lanyue.shortlink.admin.dao.mapper.UserMapper;
import com.lanyue.shortlink.admin.dto.req.UserLoginReqDTO;
import com.lanyue.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.lanyue.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.lanyue.shortlink.admin.dto.resp.UserRespDTO;
import com.lanyue.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.lanyue.shortlink.admin.common.constant.RedisCacheConstant.USER_LOGIN_KEY;

/**
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterBloomFilter;

    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        return null;
    }

    @Override
    public Boolean hasUsername(String username) {
        return userRegisterBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {
        String username = requestParam.getUsername();
        if (hasUsername(username)) {
            throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
        }
        RLock lock = redissonClient.getLock(RedisCacheConstant.LOCK_USER_REGISTER_KEY + username);
        if (!lock.tryLock()) {
            throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
        }
        try {
            UserDO userDO = new UserDO();
            BeanUtil.copyProperties(requestParam, userDO);
            if (baseMapper.insert(userDO) < 1) {
                throw new ClientException(UserErrorCodeEnum.USER_NAME_REGISTER_FAILED);
            }
            //TODO：把用户添加到默认分组中
            userRegisterBloomFilter.add(username);


        }catch (DuplicateKeyException ex) {
            throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
        }finally {
            lock.unlock();
        }

    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        String username = requestParam.getUsername();
        if (!hasUsername(username)) {
            throw new ClientException(UserErrorCodeEnum.USER_NOT_EXIST);
        }
        QueryWrapper<UserDO> wrapper = new QueryWrapper<UserDO>().eq("username", username)
                .eq("password", requestParam.getPassword());
        UserDO userDO = baseMapper.selectOne(wrapper);
        if (userDO == null) {
            throw new ClientException(UserErrorCodeEnum.USERNAME_VERIFICATION_FAILED + " 或 " + UserErrorCodeEnum.USER_PASSWORD_VERIFICATION_FAILED.getMessage());
        }
        //从redis中获取token，如果存在，说明用户已经登录，直接返回token
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(USER_LOGIN_KEY + username);
        if (CollUtil.isNotEmpty(map)) {
            stringRedisTemplate.expire(USER_LOGIN_KEY + username, 30, TimeUnit.MINUTES);
            String token = map.keySet().stream()
                    .findFirst()
                    .map(Object::toString)
                    .orElseThrow(() -> new ClientException(UserErrorCodeEnum.USER_NOT_LOGIN));
            return new UserLoginRespDTO(token);
        }
        //生成token，保存到redis中，设置过期时间
        String token = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put(USER_LOGIN_KEY + username, token, JSON.toJSONString(userDO));
        stringRedisTemplate.expire(USER_LOGIN_KEY + username, 30, TimeUnit.MINUTES);
        return new UserLoginRespDTO(token);
    }

    @Override
    public Boolean checkLogin(String username, String token) {
        return stringRedisTemplate.opsForHash().get(USER_LOGIN_KEY + username, token) != null;
    }

    @Override
    public void logout(String username, String token) {
        if (checkLogin(username, token)) {
            stringRedisTemplate.delete(USER_LOGIN_KEY + username);
            return;
        }
        throw new ClientException("用户Token不存在或用户未登录");
    }
}
