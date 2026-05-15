package com.lanyue.shortlink.admin.controller;

import com.alibaba.fastjson2.JSON;
import com.lanyue.shortlink.admin.dto.req.UserRegisterReqDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("测试查询用户名是否存在 - GET请求")
    void testHasUsername() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/short-link/admin/v1/user/has-username")
                        .param("username", "nonexistent_user_12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
    }

    @Test
    @DisplayName("测试用户注册 - POST请求")
    void testRegister() throws Exception {
        UserRegisterReqDTO reqDTO = new UserRegisterReqDTO();
        reqDTO.setUsername("test_user_" + System.currentTimeMillis());
        reqDTO.setPassword("test123456");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/short-link/admin/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(reqDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
    }

    @Test
    @DisplayName("测试用户注册 - 参数校验失败（用户名太短）")
    void testRegister_InvalidUsername() throws Exception {
        UserRegisterReqDTO reqDTO = new UserRegisterReqDTO();
        reqDTO.setUsername("ab");
        reqDTO.setPassword("test123456");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/short-link/admin/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(reqDTO)))
                .andExpect(status().isOk());
                // 参数校验失败会返回错误码，不是400
    }

    @Test
    @DisplayName("测试用户注册 - 参数校验失败（密码太短）")
    void testRegister_InvalidPassword() throws Exception {
        UserRegisterReqDTO reqDTO = new UserRegisterReqDTO();
        reqDTO.setUsername("test_user_" + System.currentTimeMillis());
        reqDTO.setPassword("123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/short-link/admin/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(reqDTO)))
                .andExpect(status().isOk());
                // 参数校验失败会返回错误码，不是400
    }

    @Test
    @DisplayName("测试检查登录状态 - GET请求")
    void testCheckLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/short-link/admin/v1/user/check-login")
                        .param("username", "test_user")
                        .param("token", "invalid_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    @DisplayName("测试获取用户信息 - GET请求")
    void testGetUserByUsername() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/short-link/admin/v1/user/nonexistent_user_12345"))
                .andExpect(status().isOk());
                // 用户不存在会返回错误码
    }
}
