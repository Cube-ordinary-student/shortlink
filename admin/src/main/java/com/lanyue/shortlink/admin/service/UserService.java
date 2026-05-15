package com.lanyue.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lanyue.shortlink.admin.dao.entity.UserDO;
import com.lanyue.shortlink.admin.dto.req.UserLoginReqDTO;
import com.lanyue.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.lanyue.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.lanyue.shortlink.admin.dto.resp.UserRespDTO;

/**
 * 用户接口层
 */
public interface UserService extends IService<UserDO> {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 查询用户名是否存在
     *
     * @param username 用户名
     * @return 存在返回true，不存在返回false
     */
    Boolean hasUsername(String username);

    /**
     * 注册用户
     *
     * @param requestParam 注册请求参数
     */
    void register(UserRegisterReqDTO requestParam);

    /**
     * 用户登录
     *
     * @param requestParam 登录请求参数
     * @return 登录响应
     */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    /**
     * 检查用户是否登录
     *
     * @param username 用户名
     * @param token    登录token
     * @return 已登录返回true，未登录返回false
     */
    Boolean checkLogin(String username, String token);

    /**
     * 用户退出登录
     *
     * @param username 用户名
     * @param token    登录token
     */
    void logout(String username, String token);
}
