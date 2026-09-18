package com.dine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.dto.SmsTemplateDto;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.repository.model.MtSmsTemplate;

import java.util.List;
import java.util.Map;

/**
 * 短信模板业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface SmsTemplateService extends IService<MtSmsTemplate> {

    /**
     * 分页查询模板列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtSmsTemplate> querySmsTemplateListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加模板
     *
     * @param reqSmsTemplateDto
     * @throws BusinessCheckException
     */
    MtSmsTemplate saveSmsTemplate(SmsTemplateDto reqSmsTemplateDto) throws BusinessCheckException;

    /**
     * 删除短信模板
     * @param id
     * @param operator
     * @return
     * */
    void deleteTemplate(Integer id, String operator) throws BusinessCheckException;

    /**
     * 根据模板ID获取模板信息
     *
     * @param id ID
     * @throws BusinessCheckException
     */
    MtSmsTemplate querySmsTemplateById(Integer id) throws BusinessCheckException;

    /**
     * 根据条件搜索模板
     * */
    List<MtSmsTemplate> querySmsTemplateByParams(Map<String, Object> params) throws BusinessCheckException;

}
