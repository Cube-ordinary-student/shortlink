package com.lanyue.shortlink.admin.dto.req;

import lombok.Data;

/**
 * 新增分组请求参数
 */
@Data
public class GroupSaveReqDTO {

    /**
     * 分组名称
     */
    private String name;
}