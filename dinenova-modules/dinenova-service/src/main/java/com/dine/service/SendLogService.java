package com.dine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.dto.ReqSendLogDto;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.repository.model.MtSendLog;

/**
 * 发券记录业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface SendLogService extends IService<MtSendLog> {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtSendLog> querySendLogListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加记录
     *
     * @param  reqSendLogDto
     * @throws BusinessCheckException
     */
    MtSendLog addSendLog(ReqSendLogDto reqSendLogDto) throws BusinessCheckException;

    /**
     * 根据组ID获取发券记录
     *
     * @param  id ID
     * @throws BusinessCheckException
     */
    MtSendLog querySendLogById(Long id) throws BusinessCheckException;

    /**
     * 删除发券记录
     *
     * @param  id       ID
     * @param  operator 操作人
     * @throws BusinessCheckException
     * @return
     */
    void deleteSendLog(Long id, String operator) throws BusinessCheckException;
}
