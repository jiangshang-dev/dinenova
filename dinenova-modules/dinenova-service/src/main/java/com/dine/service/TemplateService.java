package com.dine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.repository.model.Template;

import java.util.List;
import java.util.Map;

/**
 * 小程序全局配置业务接口
 *
 * Created by 袁腾飞老师
 * CopyRight https://www.fuint.cn
 */
public interface TemplateService extends IService<Template> {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<Template> queryTemplateListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加小程序全局配置
     *
     * @param  Template
     * @throws BusinessCheckException
     * @return
     */
    Template addTemplate(Template Template) throws BusinessCheckException;

    /**
     * 根据ID获取小程序全局配置信息
     *
     * @param storeId ID
     * @throws BusinessCheckException
     * @return
     */
    Template queryTemplateById(Integer storeId) throws BusinessCheckException;

    /**
     * 根据ID删除小程序全局配置
     *
     * @param id ID
     * @param operator 操作人
     * @throws BusinessCheckException
     * @return
     */
    void deleteMt_all_setting(Integer id, String operator) throws BusinessCheckException;

    /**
     * 更新小程序全局配置
     * @param  Template
     * @throws BusinessCheckException
     * @return
     * */
    Template updateTemplate(Template Template) throws BusinessCheckException;

    /**
     * 根据条件搜索小程序全局配置
     *
     * @param params 查询参数
     * @throws BusinessCheckException
     * @return
     * */
    List<Template> queryMt_all_settingListByParams(Map<String, Object> params) throws BusinessCheckException;
}
