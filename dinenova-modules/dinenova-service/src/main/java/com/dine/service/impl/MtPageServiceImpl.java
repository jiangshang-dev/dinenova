package com.dine.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.dto.AccountInfo;
import com.dine.dto.PageDto;
import com.dine.enums.PageEnum;
import com.dine.service.MtPageService;
import com.dine.framework.annoation.OperationServiceLog;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.repository.mapper.MtStoreMapper;
import com.dine.repository.model.MtPage;
import com.dine.repository.mapper.MtPageMapper;
import com.dine.repository.model.MtStore;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.pagehelper.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * diy首页装修服务接口
 * <p>
 * Created by 袁腾飞老师
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor
public class MtPageServiceImpl extends ServiceImpl<MtPageMapper, MtPage> implements MtPageService {

    private static final Logger logger = LoggerFactory.getLogger(MtPageServiceImpl.class);

    private MtPageMapper pageMapper;

    private MtStoreMapper storeMapper;

    /**
     * 分页查询数据列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtPage> queryMt_pageListByPagination(PaginationRequest paginationRequest) {
        Page<MtPage> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtPage> lambdaQueryWrapper = Wrappers.lambdaQuery();

        List<MtPage> dataList = pageMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtPage> paginationResponse = new PaginationResponse(pageImpl, MtPage.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 添加diy首页装修
     *
     * @param mtPage diy首页装修信息
     * @return
     */
    @Override
    @OperationServiceLog(description = "新增diy首页装修")
    public MtPage addMt_page(MtPage mtPage) throws BusinessCheckException {
        mtPage.setUpdateTime(LocalDateTime.now());
        mtPage.setCreateTime(LocalDateTime.now());
        Integer id = pageMapper.insert(mtPage);
        if (id > 0) {
            return mtPage;
        } else {
            throw new BusinessCheckException("新增diy首页装修数据失败");
        }
    }

    /**
     * 根据ID获diy首页装修取息
     *
     * @param id diy首页装修ID
     * @return
     */
    @Override
    public MtPage queryMt_pageById(Integer id) {
        return pageMapper.selectById(id);
    }

    /**
     * 根据ID删除diy首页装修
     *
     * @param id       diy首页装修ID
     * @param operator 操作人
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "删除diy首页装修")
    public void deleteMt_page(Integer id, String operator) {
        MtPage MtPage = queryMt_pageById(id);
        if (null == MtPage) {
            return;
        }
        MtPage.setUpdateTime(LocalDateTime.now());
        pageMapper.updateById(MtPage);
    }

    /**
     * 修改diy首页装修数据
     *
     * @param mtPage
     * @return
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "更新diy首页装修")
    public MtPage updateMt_page(MtPage mtPage) throws BusinessCheckException {
        mtPage = queryMt_pageById(mtPage.getPageId());
        if (mtPage == null) {
            throw new BusinessCheckException("该diy首页装修状态异常");
        }
        mtPage.setUpdateTime(LocalDateTime.now());
        pageMapper.updateById(mtPage);
        return mtPage;
    }

    /**
     * 根据条件搜索diy首页装修
     *
     * @param params 查询参数
     * @return
     * @throws BusinessCheckException
     */
    @Override
    public List<MtPage> queryMtPageListByParams(Map<String, Object> params) {
        LambdaQueryWrapper<MtPage> lambdaQueryWrapper = Wrappers.lambdaQuery();
        // if (StringUtils.isNotBlank(merchantId)) {
        //     lambdaQueryWrapper.eq(MtPage::getMerchantId, merchantId);
        // }
        List<MtPage> dataList = pageMapper.selectList(lambdaQueryWrapper);
        return dataList;
    }

    @Override
    public PageDto detail(Integer storeId, Integer type) {
        PageDto dto = new PageDto();
        // 暂存 0、发布到当前店铺 1、发布到多门店 2、使用上次 3
        if (type == 0) {
            type = PageEnum.TEMP_SAVE.getCode();
        } else if (type == 1) {
            type = PageEnum.USE_ING.getCode();
        } else if (type == 2) {
            type = PageEnum.USE_ING.getCode();
        } else if (type == 3) {
            type = PageEnum.USE_LAST.getCode();
        }
        // if (type == 0) {
        //     getPageNoUse(storeId, dto);
        // } else {
        LambdaQueryWrapper<MtPage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MtPage::getStoreId, storeId); // 查当前店铺的
        queryWrapper.eq(MtPage::getStatus, type); // 默认正在使用的
        List<MtPage> pageList = pageMapper.selectList(queryWrapper);
        if (pageList != null && !pageList.isEmpty()) {
            MtPage page = pageList.get(0);
            BeanUtils.copyProperties(page, dto);

            dto.setPageDataJson(JSON.parseObject(page.getPageData()));
            dto.setItems((JSONArray) JSON.parseObject(page.getPageData()).get("items"));
            dto.setPage((JSONObject) JSON.parseObject(page.getPageData()).get("page"));
            dto.setPageData("");
        } else {
            getPageNoUse(storeId, dto);
        }
        // }
        return dto;
    }

    private void getPageNoUse(Integer storeId, PageDto dto) {
        LambdaQueryWrapper<MtPage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MtPage::getStoreId, storeId); // 查当前店铺的
        List<MtPage> pages = pageMapper.selectList(wrapper);
        if (pages != null && !pages.isEmpty()) {
            MtPage page = pages.get(0);
            BeanUtils.copyProperties(page, dto);

            JSONObject defaultPage = getDefaultPage();
            dto.setPageDataJson(defaultPage);
            dto.setItems(new JSONArray());
            dto.setPage((JSONObject) JSON.parseObject(page.getPageData()).get("page"));
            dto.setPageData("");
        } else {
            MtPage page = new MtPage();
            JSONObject defaultPage = getDefaultPage();
            dto.setPageDataJson(defaultPage);
            dto.setItems(new JSONArray());
            dto.setPage((JSONObject) JSON.parseObject(page.getPageData()));
            dto.setPageData("");
        }
    }

    public static JSONObject getDefaultPage(){
        JSONObject pageData = new JSONObject();
        JSONObject page = new JSONObject();
        page.put("type", "page");
        page.put("name", "页面设置");
        // 参数
        JSONObject params = new JSONObject();
        params.put("icon", "icon-biaoti");
        params.put("name", "首页装修");
        params.put("shareImg", "image/diy/logo.png");
        params.put("shareTitle", "分享标题");
        params.put("title", "赤云点餐");
        params.put("titleType", "image"); //text文字 image图片
        params.put("toplogo", "image/diy/logo_top.png"); //text文字 image图片
        page.put("params", params);
        // 样式
        JSONObject style = new JSONObject();
        style.put("titleTextColor", "black");
        style.put("titleBackgroundColor", "#ff4c01");
        page.put("style", style);
        // 分类设置
        JSONObject category = new JSONObject();
        category.put("open", 1);
        category.put("color", "#FFFFFF");
        page.put("category", category);

        //pageData
        pageData.put("page", page);
        pageData.put("items", new JSONArray());
        return pageData;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean edit(Integer storeId, String params, String type, AccountInfo accountInfo) {
        // 暂存 0
        // 发布并保存到当前店铺 1
        // 发布并保存到多门店 2
        // 使用上次 3  type
        // 暂存 0、发布到当前店铺 1、发布到多门店 2、使用上次 3
        if ("3".equals(type)) { // 使用上次
            MtPage page = getPageData(storeId, PageEnum.USE_LAST.getCode());

            if (page != null) {
                // 上次页面改为发布状态
                updateLastToUsePage(storeId);
                return true;
            } else {
                return false;
            }
        } else if ("0".equals(type)) { // 暂存
            updateUseToLastPage(storeId);
            MtPage page = getPageData(storeId, PageEnum.TEMP_SAVE.getCode());
            if (page == null) {
                // 暂存
                saveNewPage(storeId, params, PageEnum.TEMP_SAVE.getCode(), "暂存");
            } else {
                page.setPageName("暂存");
                page.setPageData(params);
                pageMapper.updateById(page);
            }
        } else if ("1".equals(type)) { // 发布并保存到当前店铺
            // 查询当前店铺页面是否有已发布的页面，如果没有就新增，如果有发布的页面就把发布改上一次
            MtPage page = getPageData(storeId, PageEnum.USE_ING.getCode());
            if (page == null) {
                updateUseStatus(storeId, PageEnum.USE_LAST.getCode(), PageEnum.USE_ING.getCode(), PageEnum.USE_LAST.getCode());
                // 保存并发布新页面
                saveNewPage(storeId, params, PageEnum.USE_ING.getCode(), "直接发布1");
            } else {
                updateUseStatus(storeId, PageEnum.USE_LAST.getCode(), PageEnum.USE_ING.getCode(), PageEnum.USE_LAST.getCode());
                // 直接发布
                saveNewPage(storeId, params, PageEnum.USE_ING.getCode(), "直接发布");
            }
        } else if ("2".equals(type)) { // 发布到多门店

            Integer merchantId = accountInfo.getMerchantId();
            LambdaQueryWrapper<MtStore> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MtStore::getMerchantId, merchantId);
            List<MtStore> stores = storeMapper.selectList(wrapper);
            for (MtStore store : stores) {
                storeId = store.getId();

                MtPage page = getPageData(storeId, PageEnum.USE_ING.getCode());
                if (page == null) {
                    updateUseStatus(storeId, PageEnum.USE_LAST.getCode(), PageEnum.USE_ING.getCode(), PageEnum.USE_LAST.getCode());
                    // 保存并发布新页面
                    saveNewPage(storeId, params, PageEnum.USE_ING.getCode(), "发布");
                } else {
                    updateUseStatus(storeId, PageEnum.USE_LAST.getCode(), PageEnum.USE_ING.getCode(), PageEnum.USE_LAST.getCode());
                    // updateTempSavePage(storeId);
                    saveNewPage(storeId, params, PageEnum.USE_ING.getCode(), "直接发布2");
                }
            }
        }
        return true;
    }

    private MtPage getPageData(Integer storeId, int status) {
        LambdaQueryWrapper<MtPage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MtPage::getStoreId, storeId); // 查当前店铺的
        queryWrapper.eq(MtPage::getStatus, status); // 默认上一次，说明现在没使用的
        MtPage page = pageMapper.selectOne(queryWrapper);
        return page;
    }

    private void saveNewPage(Integer storeId, String params, int status, String name) {
        // 保存暂存数据
        MtPage mtPage = new MtPage();
        mtPage.setPageData(params);
        mtPage.setPageName(name);
        mtPage.setStoreId(storeId);
        mtPage.setStatus(status);
        mtPage.setIsDelete(0);
        mtPage.setCreateTime(LocalDateTime.now());
        mtPage.setUpdateTime(LocalDateTime.now());
        pageMapper.insert(mtPage);
    }

    /**
     * 如果有发布的页面就把发布改上一次
     *
     * @param storeId
     * @param last1
     * @param use
     * @param last2
     */
    private void updateUseStatus(Integer storeId, int last1, int use, int last2) {
        // 查询当前店铺页面是否有已发布的页面，
        // 如果没有就新增，
        // 如果有发布的页面就把发布改上一次
        // 如果有上一次的就把上一次的删除，没有上一次的页面把发布的页面改为上一次
        LambdaQueryWrapper<MtPage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MtPage::getStoreId, storeId);
        wrapper.eq(MtPage::getStatus, last1);
        pageMapper.delete(wrapper);

        MtPage mtPage = getPageData(storeId, use);
        if (mtPage != null) {
            mtPage.setPageName("上一次");
            mtPage.setStatus(last2);
            pageMapper.updateById(mtPage);
        }
    }

    /**
     * 把上一次改为已发布
     */
    private void updateLastToUsePage(Integer storeId) {
        LambdaQueryWrapper<MtPage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MtPage::getStoreId, storeId); // 查当前店铺的
        wrapper.eq(MtPage::getStatus, PageEnum.USE_LAST.getCode());
        MtPage page = pageMapper.selectOne(wrapper);
        if (page != null) {
            page.setPageName("发布");
            page.setStatus(PageEnum.USE_ING.getCode());
            pageMapper.updateById(page);
        }
    }

    /**
     * 暂存页面改发布
     *
     * @param storeId
     */
    private void updateTempSavePage(Integer storeId) {
        // 暂存页面改发布
        MtPage page = getPageData(storeId, PageEnum.TEMP_SAVE.getCode());
        if (page != null) {
            page.setPageName("发布");
            page.setStatus(PageEnum.USE_ING.getCode());
            pageMapper.updateById(page);
        }
    }

    /**
     * 查询发布状态改为上一次使用
     *
     * @param storeId
     */
    private void updateUseToLastPage(Integer storeId) {
        LambdaQueryWrapper<MtPage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MtPage::getStoreId, storeId); // 查当前店铺的
        wrapper.eq(MtPage::getStatus, PageEnum.USE_ING.getCode()); // 查询没有使用的
        MtPage mtPage = pageMapper.selectOne(wrapper);
        if (mtPage != null) {
            // 查找是否有上一次
            LambdaQueryWrapper<MtPage> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(MtPage::getStoreId, storeId);
            queryWrapper.eq(MtPage::getStatus, PageEnum.USE_LAST.getCode());
            pageMapper.delete(queryWrapper);

            mtPage.setStatus(PageEnum.USE_LAST.getCode());
            mtPage.setPageName("上一次");
            pageMapper.updateById(mtPage);
        }
    }

}
