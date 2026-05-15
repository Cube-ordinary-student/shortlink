package com.lanyue.shortlink.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lanyue.shortlink.admin.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分组持久层实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_group")
public class GroupDO extends BaseDO {

    private Long id;

    private String gid;

    private String username;

    private String name;

    private String description;

    private Integer sortOrder;
}
