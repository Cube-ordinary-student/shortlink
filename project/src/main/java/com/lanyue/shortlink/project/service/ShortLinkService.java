package com.lanyue.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lanyue.shortlink.project.dao.entity.ShortLinkDO;
import com.lanyue.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.lanyue.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.lanyue.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.lanyue.shortlink.project.dto.resp.ShortLinkPageRespDTO;

import java.util.List;

public interface ShortLinkService extends IService<ShortLinkDO> {

    /**
     * 创建短链接
     *
     * @param requestParam
     * @return
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);

    /**
     * 分页查询短链接
     *
     * @param requestParam
     * @return
     */
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

    /**
     * 查询分组短链接数量
     *
     * @param requestParam
     * @return
     */
    List<ShortLinkGroupCountQueryRespDTO> queryShortLinkGroupCount(List<String> requestParam);

    /**
     * 修改短链接
     *
     * @param requestParam
     */
    void updateShortLink(ShortLinkUpdateReqDTO requestParam);

    /**
     * 短链接跳转：根据短链接URI还原原始链接
     *
     * @param shortUri 短链接URI
     * @return 原始链接
     */
    String restoreUrl(String shortUri);

    /**
     * 根据分组标识删除分组下所有短链接
     *
     * @param gid 分组标识
     */
    void deleteByGid(String gid);

    /**
     * 根据URL获取对应网站的标题
     *
     * @param url 目标网站地址
     * @return 网站标题
     */
    String getTitleByUrl(String url);
}
