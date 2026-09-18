package com.dine.service;

import com.dine.dto.SettlementDto;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.param.backend.SettlementRequest;
import com.dine.repository.model.MtSettlement;

/**
 * 订单结算相关业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface SettlementService {

    /**
     * 分页查询结算列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtSettlement> querySettlementListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 提交结算
     *
     * @param  requestParam
     * @throws BusinessCheckException
     * @return
     */
    Boolean submitSettlement(SettlementRequest requestParam) throws BusinessCheckException;

    /**
     * 结算确认
     *
     * @param  settlementId
     * @param  operator
     * @throws BusinessCheckException
     * @return
     */
    Boolean doConfirm(Integer settlementId, String operator) throws BusinessCheckException;

    /**
     * 获取结算详情
     *
     * @param settlementId
     * @param page
     * @param pageSize
     * @return
     * */
    SettlementDto getSettlementInfo(Integer settlementId, Integer page, Integer pageSize) throws BusinessCheckException;
}