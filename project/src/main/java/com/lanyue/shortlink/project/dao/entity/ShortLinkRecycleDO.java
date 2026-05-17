package com.lanyue.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_short_link_recycle")
public class ShortLinkRecycleDO {

    private Long id;

    private Long shortLinkId;

    private String gid;

    private String shortLinkSuffix;

    private String originUrl;

    private LocalDateTime deleteTime;

    private LocalDateTime expireTime;
}
