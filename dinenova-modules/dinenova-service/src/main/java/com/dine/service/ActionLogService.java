package com.dine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.repository.model.TActionLog;

/**
 * 后台日志服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface ActionLogService extends IService<TActionLog> {

    /**
     * 保存日志
     *
     * @param actionLog
     * @return
     */
    void saveActionLog(TActionLog actionLog);

    /**
     * 获取分页查询数据
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<TActionLog> findLogsByPagination(PaginationRequest paginationRequest);
}
