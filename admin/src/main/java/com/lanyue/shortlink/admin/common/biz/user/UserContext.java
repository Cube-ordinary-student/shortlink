package com.lanyue.shortlink.admin.common.biz.user;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Optional;

/**
 * 用户上下文
 */
public class UserContext {

    private static final ThreadLocal<UserInfoDTO> USER_THREAD_LOCAL = new TransmittableThreadLocal<>();

    public static void setUser(UserInfoDTO user) {
        USER_THREAD_LOCAL.set(user);
    }

    public static UserInfoDTO getUser() {
        return USER_THREAD_LOCAL.get();
    }

    public static String getUserId() {
        return Optional.ofNullable(USER_THREAD_LOCAL.get())
                .map(UserInfoDTO::getUserId)
                .orElse(null);
    }

    public static String getUsername() {
        return Optional.ofNullable(USER_THREAD_LOCAL.get())
                .map(UserInfoDTO::getUsername)
                .orElse(null);
    }

    public static String getRealName() {
        return Optional.ofNullable(USER_THREAD_LOCAL.get())
                .map(UserInfoDTO::getRealName)
                .orElse(null);
    }

    public static void removeUser() {
        USER_THREAD_LOCAL.remove();
    }
}
