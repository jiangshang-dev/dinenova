package com.dine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.dto.CommissionLogDto;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.param.backend.CommissionLogRequest;
import com.dine.repository.model.MtCommissionLog;

/**
 * 分销提成记录业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface CommissionLogService extends IService<MtCommissionLog> {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<CommissionLogDto> queryCommissionLogByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 计算订单分销提成
     *
     * @param  orderId 订单ID
     * @throws BusinessCheckException
     * @return
     */
    void calculateCommission(Integer orderId) throws BusinessCheckException;

    /**
     * 根据ID获取记录信息
     *
     * @param  id 记录ID
     * @throws BusinessCheckException
     * @return
     */
    CommissionLogDto queryCommissionLogById(Integer id) throws BusinessCheckException;

    /**
     * 更新分销提成记录
     *
     * @param requestParam 请求参数
     * @throws BusinessCheckException
     * @return
     */
    void updateCommissionLog(CommissionLogRequest requestParam) throws BusinessCheckException;
}
