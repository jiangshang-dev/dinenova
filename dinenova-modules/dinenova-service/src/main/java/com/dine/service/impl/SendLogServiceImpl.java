package com.dine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.dto.ReqSendLogDto;
import com.dine.enums.StatusEnum;
import com.dine.service.SendLogService;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.repository.mapper.MtSendLogMapper;
import com.dine.repository.model.MtSendLog;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

/**
 * 发送卡券记录业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor
public class SendLogServiceImpl extends ServiceImpl<MtSendLogMapper, MtSendLog> implements SendLogService {

    private MtSendLogMapper mtSendLogMapper;

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtSendLog> querySendLogListByPagination(PaginationRequest paginationRequest) {
        Page<MtSendLog> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtSendLog> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtSendLog::getStatus, StatusEnum.DISABLE.getKey());

        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtSendLog::getStatus, status);
        }
        String userId = paginationRequest.getSearchParams().get("userId") == null ? "" : paginationRequest.getSearchParams().get("userId").toString();
        if (StringUtils.isNotBlank(userId)) {
            lambdaQueryWrapper.eq(MtSendLog::getUserId, userId);
        }
        String merchantId = paginationRequest.getSearchParams().get("merchantId") == null ? "" : paginationRequest.getSearchParams().get("merchantId").toString();
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtSendLog::getMerchantId, merchantId);
        }
        String storeId = paginationRequest.getSearchParams().get("storeId") == null ? "" : paginationRequest.getSearchParams().get("storeId").toString();
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.eq(MtSendLog::getStoreId, storeId);
        }
        String couponId = paginationRequest.getSearchParams().get("couponId") == null ? "" : paginationRequest.getSearchParams().get("couponId").toString();
        if (StringUtils.isNotBlank(couponId)) {
            lambdaQueryWrapper.eq(MtSendLog::getCouponId, couponId);
        }
        String mobile = paginationRequest.getSearchParams().get("mobile") == null ? "" : paginationRequest.getSearchParams().get("mobile").toString();
        if (StringUtils.isNotBlank(mobile)) {
            lambdaQueryWrapper.eq(MtSendLog::getMobile, mobile);
        }

        lambdaQueryWrapper.orderByDesc(MtSendLog::getId);
        List<MtSendLog> dataList = mtSendLogMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtSendLog> paginationResponse = new PaginationResponse(pageImpl, MtSendLog.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 添加发放记录
     *
     * @param reqSendLogDto
     * @throws BusinessCheckException
     * @return
     */
    @Override
    public MtSendLog addSendLog(ReqSendLogDto reqSendLogDto) {
        MtSendLog mtLog = new MtSendLog();
        mtLog.setMerchantId(reqSendLogDto.getMerchantId());
        mtLog.setStoreId(reqSendLogDto.getStoreId());
        mtLog.setType(reqSendLogDto.getType());
        mtLog.setUserId(reqSendLogDto.getUserId());
        mtLog.setFileName(reqSendLogDto.getFileName());
        mtLog.setFilePath(reqSendLogDto.getFilePath());
        mtLog.setMobile(reqSendLogDto.getMobile());
        mtLog.setCouponId(reqSendLogDto.getCouponId());
        mtLog.setGroupId(reqSendLogDto.getGroupId());
        mtLog.setGroupName(reqSendLogDto.getGroupName());
        mtLog.setSendNum(reqSendLogDto.getSendNum());
        mtLog.setRemoveSuccessNum(0);
        mtLog.setRemoveFailNum(0);
        mtLog.setStatus(StatusEnum.ENABLED.getKey());
        mtLog.setCreateTime(new Date());
        mtLog.setOperator(reqSendLogDto.getOperator());
        mtLog.setUuid(reqSendLogDto.getUuid());
        mtSendLogMapper.insert(mtLog);
        return mtLog;
    }

    /**
     * 根据ID查询发券记录
     *
     * @param id 发券记录ID
     * @throws BusinessCheckException
     * @return
     */
    @Override
    public MtSendLog querySendLogById(Long id) {
        return mtSendLogMapper.selectById(id.intValue());
    }

    /**
     * 根据ID删除发券记录
     *
     * @param  id       发券记录ID
     * @param  operator 操作人
     * @throws BusinessCheckException
     * @return
     */
    @Override
    public void deleteSendLog(Long id, String operator) {
        MtSendLog couponGroup = querySendLogById(id);

        if (null == couponGroup) {
            return;
        }

        couponGroup.setStatus(StatusEnum.DISABLE.getKey());
        couponGroup.setOperator(operator);
        mtSendLogMapper.updateById(couponGroup);
    }
}
