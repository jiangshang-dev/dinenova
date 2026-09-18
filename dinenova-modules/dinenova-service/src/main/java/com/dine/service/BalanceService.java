package com.dine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.dto.AccountInfo;
import com.dine.dto.BalanceDto;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.repository.model.MtBalance;
import java.util.List;

/**
 * 余额业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface BalanceService extends IService<MtBalance> {

    /**
     * 分页查询余额列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<BalanceDto> queryBalanceListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加余额记录
     *
     * @param reqDto
     * @param updateBalance
     * @throws BusinessCheckException
     */
    Boolean addBalance(MtBalance reqDto, Boolean updateBalance) throws BusinessCheckException;

    /**
     * 发放余额
     *
     * @param accountInfo
     * @param object
     * @param userIds
     * @param amount
     * @param remark
     * @return
     */
    void distribute(AccountInfo accountInfo, String object, String userIds, String amount, String remark) throws BusinessCheckException;

    /**
     * 获取订单余额记录
     *
     * @param orderSn
     * @return
     * */
    List<MtBalance> getBalanceListByOrderSn(String orderSn) throws BusinessCheckException;
}
