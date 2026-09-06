package com.lanyue.shortlink.admin.common.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseDO {
    private Date createTime;

    private Date updateTime;

}
