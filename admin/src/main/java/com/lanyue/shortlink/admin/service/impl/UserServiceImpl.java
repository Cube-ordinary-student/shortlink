package com.lanyue.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
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
import org.springframework.stereotype.Service;

/**
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterBloomFilter;

    private final RedissonClient redissonClient;

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
        return null;
    }

    @Override
    public Boolean checkLogin(String username, String token) {
        return null;
    }

    @Override
    public void logout(String username, String token) {

    }
}
