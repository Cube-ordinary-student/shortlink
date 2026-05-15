package com.lanyue.shortlink.admin.common.biz.user;

import com.lanyue.shortlink.admin.common.constant.CommonConstant;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jodd.util.StringUtil;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class UserTransmitFilter implements Filter {
    @Override
    @SneakyThrows
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String username = httpServletRequest.getHeader(CommonConstant.USERNAME);
        if (StringUtil.isNotBlank(username)) {
            String userId = httpServletRequest.getHeader(CommonConstant.USER_ID);
            String realName = httpServletRequest.getHeader(CommonConstant.REAL_NAME);
            UserInfoDTO userInfoDTO = new UserInfoDTO(userId, username, realName);
            UserContext.setUser(userInfoDTO);
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }
}
