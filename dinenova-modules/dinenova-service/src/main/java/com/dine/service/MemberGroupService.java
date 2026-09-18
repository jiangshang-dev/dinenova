package com.dine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.dto.MemberGroupDto;
import com.dine.dto.UserGroupDto;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.repository.model.MtUserGroup;

/**
 * 会员分组业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MemberGroupService extends IService<MtUserGroup> {

    /**
     * 分页查询分组列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<UserGroupDto> queryMemberGroupListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 新增会员分组
     *
     * @param  memberGroupDto
     * @throws BusinessCheckException
     */
    MtUserGroup addMemberGroup(MemberGroupDto memberGroupDto) throws BusinessCheckException;

    /**
     * 修改卡券分组
     *
     * @param  memberGroupDto
     * @throws BusinessCheckException
     */
    MtUserGroup updateMemberGroup(MemberGroupDto memberGroupDto) throws BusinessCheckException;

    /**
     * 根据组ID获取分组信息
     *
     * @param  id 分组ID
     * @throws BusinessCheckException
     */
    MtUserGroup queryMemberGroupById(Integer id) throws BusinessCheckException;

    /**
     * 根据分组ID删除分组信息
     *
     * @param  id 分组ID
     * @param  operator 操作人
     * @throws BusinessCheckException
     */
    void deleteMemberGroup(Integer id, String operator) throws BusinessCheckException;
}
