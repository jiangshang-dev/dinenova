package com.dine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.service.TemplateService;
import com.dine.framework.annoation.OperationServiceLog;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.repository.mapper.TemplateMapper;
import com.dine.repository.mapper.MtMerchantMapper;
import com.dine.repository.model.Template;
import com.dine.enums.StatusEnum;
import com.dine.repository.model.MtMerchant;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.pagehelper.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 小程序全局配置服务接口
 */
@Service
@AllArgsConstructor
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper, Template> implements TemplateService {

    private static final Logger logger = LoggerFactory.getLogger(TemplateServiceImpl.class);

    private TemplateMapper templateMapper;

    private MtMerchantMapper mtMerchantMapper;

    /**
     * 分页查询数据列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<Template> queryTemplateListByPagination(PaginationRequest paginationRequest) {
        Page<Template> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<Template> lambdaQueryWrapper = Wrappers.lambdaQuery();

        Map<String, Object> searchParams = paginationRequest.getSearchParams();
        if (searchParams.size() != 0) {
            Object merchantId = searchParams.get("merchantId");
            lambdaQueryWrapper.eq(Template::getMerchantId, merchantId);
        }
        // lambdaQueryWrapper.ne(Template::getStatus, StatusEnum.DISABLE.getKey());
        List<Template> dataList = templateMapper.selectList(lambdaQueryWrapper);

        for (Template mtStore : dataList) {
            MtMerchant mtMerchant = mtMerchantMapper.selectById(mtStore.getMerchantId());
            if (mtMerchant != null) {
                mtStore.setMerchantName(mtMerchant.getName());
                mtStore.setStoreId(mtMerchant.getId());
            }
        }

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<Template> paginationResponse = new PaginationResponse(pageImpl, Template.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 添加
     *
     * @param Template 添加
     * @return
     */
    @Override
    @OperationServiceLog(description = "新增")
    public Template addTemplate(Template Template) throws BusinessCheckException {
        Template.setStatus(StatusEnum.FORBIDDEN.getKey());
        Template.setUpdateTime(LocalDateTime.now());
        Template.setCreateTime(LocalDateTime.now());
        Integer id = templateMapper.insert(Template);
        if (id > 0) {
            return Template;
        } else {
            throw new BusinessCheckException("新增数据失败");
        }
    }

    /**
     * 根据id查询
     *
     * @param storeId ID
     * @return
     */
    @Override
    public Template queryTemplateById(Integer storeId) {
        LambdaQueryWrapper<Template> templateLambdaQueryWrapper = new LambdaQueryWrapper<>();
        templateLambdaQueryWrapper.eq(storeId != null, Template::getStoreId, storeId);
        Template template = templateMapper.selectOne(templateLambdaQueryWrapper);
        return template;
    }

    /**
     * 根据ID删除
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "删除")
    public void deleteMt_all_setting(Integer id, String operator) {
        Template Template = queryTemplateById(id);
        if (null == Template) {
            return;
        }
        Template.setStatus(StatusEnum.DISABLE.getKey());
        Template.setUpdateTime(LocalDateTime.now());
        templateMapper.updateById(Template);
    }

    /**
     * 修改小程序全局配置数据
     *
     * @param Template
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "更新小程序全局配置")
    public Template updateTemplate(Template Template) throws BusinessCheckException {
        // Template = queryMt_all_settingById(Template.getId());
        if (Template == null) {
            throw new BusinessCheckException("该小程序全局配置状态异常");
        }
        Template.setUpdateTime(LocalDateTime.now());
        templateMapper.updateById(Template);
        return Template;
    }

   /**
    * 根据条件搜索小程序全局配置
    *
    * @param params 查询参数
    * @throws BusinessCheckException
    * @return
    * */
    @Override
    public List<Template> queryMt_all_settingListByParams(Map<String, Object> params) {
        String status =  params.get("status") == null ? StatusEnum.ENABLED.getKey(): params.get("status").toString();
        String storeId =  params.get("storeId") == null ? "" : params.get("storeId").toString();
        String merchantId =  params.get("merchantId") == null ? "" : params.get("merchantId").toString();

        LambdaQueryWrapper<Template> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(Template::getStatus, status);
        }
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(Template::getMerchantId, merchantId);
        }
        // if (StringUtils.isNotBlank(storeId)) {
        //     lambdaQueryWrapper.and(wq -> wq
        //             .eq(Template::getStoreId, 0)
        //             .or()
        //             .eq(Template::getStoreId, storeId));
        // }

        List<Template> dataList = templateMapper.selectList(lambdaQueryWrapper);
        return dataList;
    }
}
