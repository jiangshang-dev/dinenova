package com.dine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.repository.model.MtQrBackground;

import java.util.List;

/**
 * 店铺二维码背景
 */
public interface QrBackgroundService extends IService<MtQrBackground> {

    List<MtQrBackground> listByStore(Integer storeId);

    long countActive(Integer storeId);
}
