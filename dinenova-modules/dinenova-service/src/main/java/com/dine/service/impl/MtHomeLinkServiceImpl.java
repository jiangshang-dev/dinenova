package com.dine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.service.MtHomeLinkService;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.repository.mapper.MtHomeLinkMapper;
import com.dine.repository.model.MtHomeLink;
import com.dine.repository.model.MtHomeLink;
import com.dine.repository.model.MtStore;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MtHomeLinkServiceImpl extends ServiceImpl<MtHomeLinkMapper, MtHomeLink> implements MtHomeLinkService {
    private MtHomeLinkMapper homeLinkMapper;
    @Override
    public PaginationResponse<MtHomeLink> queryHomeLinkListByPagination(PaginationRequest paginationRequest) {
        Page<MtHomeLink> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());

        String name = (String) paginationRequest.getSearchParams().get("name");

        LambdaQueryWrapper<MtHomeLink> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if (name != null && !name.isEmpty()) {
            lambdaQueryWrapper.like(MtHomeLink::getName, name);
        }

        lambdaQueryWrapper.eq(MtHomeLink::getStatus, "A");

        lambdaQueryWrapper.orderByDesc(MtHomeLink::getCreateTime);
        List<MtHomeLink> dataList = homeLinkMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtHomeLink> paginationResponse = new PaginationResponse(pageImpl, MtHomeLink.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);
        return paginationResponse;
    }
}
