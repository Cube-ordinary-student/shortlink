package com.lanyue.shortlink.project.dto.req;

import com.lanyue.shortlink.project.common.web.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShortLinkPageReqDTO extends PageRequest {

    private String gid;

    private LocalDate startDate;

    private LocalDate endDate;
}
