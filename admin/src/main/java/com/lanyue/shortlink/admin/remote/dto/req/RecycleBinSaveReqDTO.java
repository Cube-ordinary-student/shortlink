package com.lanyue.shortlink.admin.remote.dto.req;

import lombok.Data;

@Data
public class RecycleBinSaveReqDTO {

    private String gid;

    private String fullShortUrl;
}