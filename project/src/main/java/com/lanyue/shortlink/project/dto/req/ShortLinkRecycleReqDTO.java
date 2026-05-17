package com.lanyue.shortlink.project.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShortLinkRecycleReqDTO {

    @NotNull(message = "短链接ID不能为空")
    private Long id;
}
