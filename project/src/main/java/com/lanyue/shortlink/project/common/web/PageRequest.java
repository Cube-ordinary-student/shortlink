package com.lanyue.shortlink.project.common.web;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long pageNum = 1L;

    private Long pageSize = 10L;
}
