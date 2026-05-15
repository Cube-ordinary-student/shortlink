package com.lanyue.shortlink.admin.common.constant;

/**
 * Redis 缓存常量类
 */
public class RedisCacheConstant {

    public static final String LOCK_USER_REGISTER_KEY = "short-link:lock:user-register:";

    public static final String USER_LOGIN_KEY = "short-link:login:";

    public static final String USER_GROUP_KEY = "short-link:user-group:";

    public static final String LOCK_GROUP_CREATE_KEY = "short-link:lock:group-create:";

    public static final String LOCK_USER_DELETE_KEY = "short-link:lock:user-delete:";
}
