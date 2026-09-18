package com.dine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.repository.model.MtHomeLink;

public interface MtHomeLinkService extends IService<MtHomeLink> {
    PaginationResponse<MtHomeLink> queryHomeLinkListByPagination(PaginationRequest paginationRequest);

}
