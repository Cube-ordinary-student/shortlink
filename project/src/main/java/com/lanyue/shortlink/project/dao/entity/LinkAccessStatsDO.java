package com.lanyue.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lanyue.shortlink.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_link_access_stats")
public class LinkAccessStatsDO extends BaseDO {

    private Long id;

    private String shortLinkSuffix;

    private LocalDate date;

    private Long pv;

    private Long uv;

    private Long ipCount;
}
