package com.lanyue.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lanyue.shortlink.admin.dao.entity.GroupDO;
import com.lanyue.shortlink.admin.dto.req.GroupUpdateReqDTO;
import com.lanyue.shortlink.admin.dto.req.ShortLinkGroupReqDTO;
import com.lanyue.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

/**
 * 分组接口层
 */
public interface GroupService extends IService<GroupDO> {

    /**
     * 查询用户的分组列表
     *
     * @return 分组列表
     */
    List<ShortLinkGroupRespDTO> listGroup();

    /**
     * 创建分组
     *
     * @param requestParam 创建分组请求参数
     */
    void saveGroup(String username, String groupName);

    /**
     * 更新分组
     *
     * @param requestParam 更新分组请求参数
     */
    void updateGroup(GroupUpdateReqDTO requestParam);

    /**
     * 删除分组
     *
     * @param gid 删除分组请求参数
     */
    void deleteGroup(String gid);

    /**
     * 排序分组
     */
    void sortGroup(List<ShortLinkGroupReqDTO> requestParam);
}
