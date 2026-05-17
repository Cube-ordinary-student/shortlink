package com.lanyue.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lanyue.shortlink.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_short_link")
public class ShortLinkDO extends BaseDO {

    private Long id;

    private String gid;

    private String shortLinkSuffix;

    private String originUrl;

    private String domain;

    private Long pv;

    private Long uv;

    private Integer status;

    private LocalDateTime expireTime;
}
