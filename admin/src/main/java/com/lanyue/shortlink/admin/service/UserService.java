package com.lanyue.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lanyue.shortlink.admin.dao.entity.UserDO;
import com.lanyue.shortlink.admin.dto.req.UserActualRespDTO;
import com.lanyue.shortlink.admin.dto.req.UserLoginReqDTO;
import com.lanyue.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.lanyue.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.lanyue.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.lanyue.shortlink.admin.dto.resp.UserRespDTO;

/**
 * 用户服务接口
 */
public interface UserService extends IService<UserDO> {

    /**
     * 根据用户名查询用户信息（脱敏）
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 根据用户名查询无脱敏用户信息
     */
    UserActualRespDTO getActualUserByUsername(String username);

    /**
     * 查询用户名是否存在
     */
    Boolean hasUsername(String username);

    /**
     * 用户注册
     */
    void register(UserRegisterReqDTO requestParam);

    /**
     * 修改用户信息
     */
    void update(UserUpdateReqDTO requestParam);

    /**
     * 用户登录
     */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    /**
     * 检查用户登录状态
     */
    Boolean checkLogin(String username, String token);

    /**
     * 用户退出登录
     */
    void logout(String username, String token);
}