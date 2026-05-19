package com.lanyue.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanyue.shortlink.admin.common.biz.user.UserContext;
import com.lanyue.shortlink.admin.common.constant.RedisKeyConstant;
import com.lanyue.shortlink.admin.common.convention.exception.ClientException;
import com.lanyue.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.lanyue.shortlink.admin.dao.entity.UserDO;
import com.lanyue.shortlink.admin.dao.mapper.UserMapper;
import com.lanyue.shortlink.admin.dto.req.*;
import com.lanyue.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.lanyue.shortlink.admin.dto.resp.UserRespDTO;
import com.lanyue.shortlink.admin.service.GroupService;
import com.lanyue.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterBloomFilter;

    private final RedissonClient redissonClient;

    private final StringRedisTemplate stringRedisTemplate;

    private final GroupService groupService;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        if (!hasUsername(username)) {
            throw new ClientException(UserErrorCodeEnum.USER_NULL);
        }
        QueryWrapper<UserDO> wrapper = new QueryWrapper<UserDO>().eq("username", username);
        UserDO userDO = baseMapper.selectOne(wrapper);
        UserRespDTO userRespDTO = new UserRespDTO();
        BeanUtil.copyProperties(userDO, userRespDTO);
        return userRespDTO;
    }

    @Override
    public UserActualRespDTO getActualUserByUsername(String username) {
        UserRespDTO userRespDTO = getUserByUsername(username);
        UserActualRespDTO userActualRespDTO = new UserActualRespDTO();
        BeanUtil.copyProperties(userRespDTO, userActualRespDTO);
        return userActualRespDTO;
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
        RLock lock = redissonClient.getLock(RedisKeyConstant.LOCK_USER_REGISTER_KEY + username);
        if (!lock.tryLock()) {
            throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
        }
        try {
            UserDO userDO = new UserDO();
            BeanUtil.copyProperties(requestParam, userDO);
            if (baseMapper.insert(userDO) < 1) {
                throw new ClientException(UserErrorCodeEnum.USER_SAVE_ERROR);
            }
            groupService.saveGroup(requestParam.getUsername(),"默认分组");
            userRegisterBloomFilter.add(username);
        }catch (DuplicateKeyException ex) {
            throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
        }finally {
            lock.unlock();
        }
    }

    public void update(UserUpdateReqDTO requestParam) {
        if (!Objects.equals(requestParam.getUsername(), UserContext.getUsername())) {
            throw new ClientException("当前登录用户修改请求异常");
        }
        UserDO userDO = new UserDO();
        BeanUtil.copyProperties(requestParam, userDO);
        LambdaUpdateChainWrapper<UserDO> wrapper = lambdaUpdate().eq(UserDO::getUsername, requestParam.getUsername());
        baseMapper.update(userDO, wrapper);
    }
    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        String username = requestParam.getUsername();
        if (!hasUsername(username)) {
            throw new ClientException(UserErrorCodeEnum.USER_NULL);
        }
        QueryWrapper<UserDO> wrapper = new QueryWrapper<UserDO>().eq("username", username)
                .eq("password", requestParam.getPassword());
        UserDO userDO = baseMapper.selectOne(wrapper);
        if (userDO == null) {
            throw new ClientException(UserErrorCodeEnum.USER_USERNAME_OR_PASSWORD_ERROR);
        }
        //从redis中获取token，如果存在，说明用户已经登录，直接返回token
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(RedisKeyConstant.USER_LOGIN_KEY + username);
        if (CollUtil.isNotEmpty(map)) {
            stringRedisTemplate.expire(RedisKeyConstant.USER_LOGIN_KEY + username, 30, TimeUnit.MINUTES);
            String token = map.keySet().stream()
                    .findFirst()
                    .map(Object::toString)
                    .orElseThrow(() -> new ClientException("用户登录错误"));
            return new UserLoginRespDTO(token);
        }
        //生成token，保存到redis中，设置过期时间
        String token = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put(RedisKeyConstant.USER_LOGIN_KEY + username, token, JSON.toJSONString(userDO));
        stringRedisTemplate.expire(RedisKeyConstant.USER_LOGIN_KEY + username, 30, TimeUnit.MINUTES);
        return new UserLoginRespDTO(token);
    }

    @Override
    public Boolean checkLogin(String username, String token) {
        return stringRedisTemplate.opsForHash().get(RedisKeyConstant.USER_LOGIN_KEY + username, token) != null;
    }

    @Override
    public void logout(String username, String token) {
        if (checkLogin(username, token)) {
            stringRedisTemplate.delete(RedisKeyConstant.USER_LOGIN_KEY + username);
            return;
        }
        throw new ClientException("用户Token不存在或用户未登录");
    }
}
