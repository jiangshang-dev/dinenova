package com.dine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.dto.AccountInfo;
import com.dine.dto.PageDto;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.repository.model.MtPage;
import com.dine.framework.exception.BusinessCheckException;
import java.util.List;
import java.util.Map;

/**
 * diy首页装修业务接口
 *
 * Created by 袁腾飞老师
 * CopyRight https://www.fuint.cn
 */
public interface MtPageService extends IService<MtPage> {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtPage> queryMt_pageListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加diy首页装修
     *
     * @param  MtPage
     * @throws BusinessCheckException
     * @return
     */
    MtPage addMt_page(MtPage MtPage) throws BusinessCheckException;

    /**
     * 根据ID获取diy首页装修信息
     *
     * @param id ID
     * @throws BusinessCheckException
     * @return
     */
    MtPage queryMt_pageById(Integer id) throws BusinessCheckException;

    /**
     * 根据ID删除diy首页装修
     *
     * @param id ID
     * @param operator 操作人
     * @throws BusinessCheckException
     * @return
     */
    void deleteMt_page(Integer id, String operator) throws BusinessCheckException;

    /**
     * 更新diy首页装修
     * @param  MtPage
     * @throws BusinessCheckException
     * @return
     * */
    MtPage updateMt_page(MtPage MtPage) throws BusinessCheckException;

    /**
     * 根据条件搜索diy首页装修
     *
     * @param params 查询参数
     * @throws BusinessCheckException
     * @return
     * */
    List<MtPage> queryMtPageListByParams(Map<String, Object> params) throws BusinessCheckException;

    PageDto detail(Integer storeId, Integer type);

    boolean edit(Integer storeId, String params, String type, AccountInfo accountInfo);
}
