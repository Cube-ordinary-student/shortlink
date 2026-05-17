package com.lanyue.shortlink.project.dto.req;

import com.lanyue.shortlink.project.common.web.PageRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShortLinkPageReqDTO extends PageRequest {
    @NotBlank(message = "gid不能为空")
    private String gid;

    private LocalDate startDate;

    private LocalDate endDate;
}
