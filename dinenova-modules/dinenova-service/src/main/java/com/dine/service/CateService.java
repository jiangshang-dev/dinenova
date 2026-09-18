package com.dine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.dto.GoodsCateDto;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.repository.model.MtGoodsCate;
import java.util.List;
import java.util.Map;

/**
 * 商品分类业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface CateService extends IService<MtGoodsCate> {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<GoodsCateDto> queryCateListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加商品分类
     *
     * @param  reqDto 分类参数
     * @throws BusinessCheckException
     * @return
     */
    MtGoodsCate addCate(MtGoodsCate reqDto) throws BusinessCheckException;

    /**
     * 根据ID获取商品分类信息
     *
     * @param  id ID
     * @throws BusinessCheckException
     */
    MtGoodsCate queryCateById(Integer id) throws BusinessCheckException;

    /**
     * 根据ID删除
     *
     * @param  id 分类ID
     * @param  operator 操作人
     * @throws BusinessCheckException
     * @return
     */
    void deleteCate(Integer id, String operator) throws BusinessCheckException;

    /**
     * 更新分类
     * @param  reqDto 分类参数
     * @throws BusinessCheckException
     * @return
     * */
    MtGoodsCate updateCate(MtGoodsCate reqDto) throws BusinessCheckException;

    /**
     * 根据条件搜索分类
     *
     * @param params 查询参数
     * @return
     * */
    List<MtGoodsCate> queryCateListByParams(Map<String, Object> params) throws BusinessCheckException;
}
