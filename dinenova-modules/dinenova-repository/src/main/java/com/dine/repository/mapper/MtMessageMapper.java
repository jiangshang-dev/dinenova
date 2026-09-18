package com.dine.repository.mapper;

import com.dine.repository.model.MtMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *  消息 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MtMessageMapper extends BaseMapper<MtMessage> {

    List<MtMessage> findNewMessage(@Param("userId") Integer userId, @Param("type") String type);

    List<MtMessage> findNeedSendMessage(@Param("type") String type);
}
