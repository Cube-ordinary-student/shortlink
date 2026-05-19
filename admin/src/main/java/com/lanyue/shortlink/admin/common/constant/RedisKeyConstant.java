package com.lanyue.shortlink.admin.common.constant;

public class RedisKeyConstant {

    /**
     * 用户注册分布式锁 Key，后面拼接用户名
     */
    public static final String LOCK_USER_REGISTER_KEY = "short-link:lock_user-regiseter:";

    /**
     * 分组创建分布式锁 Key，后面拼接用户名
     */
    public static final String LOCK_GROUP_CREATE_KEY = "short-link:lock_group-create:%s";

    /**
     * 用户登录信息 Key，后面拼接用户名
     */
    public static final String USER_LOGIN_KEY = "short-link:login:";

}
