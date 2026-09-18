package com.dine.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.repository.mapper.MtQrBackgroundMapper;
import com.dine.repository.model.MtQrBackground;
import com.dine.service.QrBackgroundService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 店铺二维码背景
 */
@Service
public class QrBackgroundServiceImpl extends ServiceImpl<MtQrBackgroundMapper, MtQrBackground> implements QrBackgroundService {

    @Override
    public List<MtQrBackground> listByStore(Integer storeId) {
        if (storeId == null || storeId < 1) {
            return Collections.emptyList();
        }
        return list(Wrappers.<MtQrBackground>lambdaQuery()
                .eq(MtQrBackground::getStoreId, storeId)
                .eq(MtQrBackground::getStatus, "A")
                .orderByAsc(MtQrBackground::getId));
    }

    @Override
    public long countActive(Integer storeId) {
        if (storeId == null || storeId < 1) {
            return 0;
        }
        return count(Wrappers.<MtQrBackground>lambdaQuery()
                .eq(MtQrBackground::getStoreId, storeId)
                .eq(MtQrBackground::getStatus, "A"));
    }
}
