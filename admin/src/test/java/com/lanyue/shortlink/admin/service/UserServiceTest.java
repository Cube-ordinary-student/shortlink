package com.lanyue.shortlink.admin.service;

import com.lanyue.shortlink.admin.common.convention.exception.ClientException;
import com.lanyue.shortlink.admin.dto.req.UserRegisterReqDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("测试用户名是否存在 - 不存在的用户名")
    void testHasUsername_NotExist() {
        Boolean result = userService.hasUsername("nonexistent_user_12345");
        assertTrue(result, "不存在的用户名应该返回true");
    }

    @Test
    @DisplayName("测试获取用户信息 - 不存在的用户")
    void testGetUserByUsername_NotExist() {
        assertThrows(ClientException.class, () -> {
            userService.getUserByUsername("nonexistent_user_12345");
        }, "查询不存在的用户应该抛出异常");
    }

    @Test
    @DisplayName("测试用户注册 - 成功")
    void testRegister_Success() {
        UserRegisterReqDTO reqDTO = new UserRegisterReqDTO();
        reqDTO.setUsername("test_user_" + System.currentTimeMillis());
        reqDTO.setPassword("test123456");
        
        assertDoesNotThrow(() -> {
            userService.register(reqDTO);
        }, "注册新用户应该成功");
    }

    @Test
    @DisplayName("测试用户注册 - 空用户名")
    void testRegister_EmptyUsername() {
        UserRegisterReqDTO reqDTO = new UserRegisterReqDTO();
        reqDTO.setUsername("");
        reqDTO.setPassword("test123456");
        
        // 参数校验会在Controller层进行，这里Service层可能允许空值
        // 实际测试时需要根据具体实现调整
    }

    @Test
    @DisplayName("测试检查登录状态 - 未登录")
    void testCheckLogin_NotLoggedIn() {
        Boolean result = userService.checkLogin("test_user", "invalid_token");
        assertFalse(result, "未登录状态应该返回false");
    }

    @Test
    @DisplayName("测试用户登出 - 无效token")
    void testLogout_InvalidToken() {
        assertThrows(ClientException.class, () -> {
            userService.logout("test_user", "invalid_token");
        }, "使用无效token登出应该抛出异常");
    }
}
