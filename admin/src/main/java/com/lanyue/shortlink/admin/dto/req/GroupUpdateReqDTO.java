package com.lanyue.shortlink.admin.dto.req;

import lombok.Data;

/**
 * 修改分组请求参数
 */
@Data
public class GroupUpdateReqDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;
}