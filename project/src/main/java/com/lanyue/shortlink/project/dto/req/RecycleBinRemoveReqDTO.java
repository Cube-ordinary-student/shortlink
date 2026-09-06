package com.lanyue.shortlink.project.dto.req;

import lombok.Data;

@Data
public class RecycleBinRemoveReqDTO {

    private String gid;

    private String fullShortUrl;
}