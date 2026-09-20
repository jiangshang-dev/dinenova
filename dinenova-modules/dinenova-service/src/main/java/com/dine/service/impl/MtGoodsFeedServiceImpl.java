package com.dine.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.repository.mapper.MtGoodsFeedMapper;
import com.dine.repository.model.MtGoodsFeed;
import com.dine.service.MtGoodsFeedService;
import org.springframework.stereotype.Service;

/**
 * 商品加料服务实现
 */
@Service
public class MtGoodsFeedServiceImpl extends ServiceImpl<MtGoodsFeedMapper, MtGoodsFeed> implements MtGoodsFeedService {
}
