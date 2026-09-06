package com.lanyue.shortlink.admin.remote.dto.req;

import lombok.Data;

import java.util.Date;

@Data
public class ShortLinkUpdateReqDTO {

    private String originUrl;

    private String fullShortUrl;

    private String originGid;

    private String gid;

    private Integer validDateType;

    private Date validDate;

    private String describe;
}