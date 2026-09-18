package com.dine.service;

import com.dine.dto.OpenGiftDto;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.model.MtOpenGift;
import java.util.Map;

/**
 * 开卡赠礼接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface OpenGiftService {

    /**
     * 获取用户的开卡赠礼
     *
     * @param paramMap 查询参数
     * @throws BusinessCheckException
     * @return
     * */
    ResponseObject getOpenGiftList(Map<String, Object> paramMap) throws BusinessCheckException;

    /**
     * 新增开卡赠礼
     *
     * @param reqDto
     * @throws BusinessCheckException
     * @return
     */
    MtOpenGift addOpenGift(MtOpenGift reqDto) throws BusinessCheckException;

    /**
     * 根据ID获取开卡赠礼
     *
     * @param id ID
     * @throws BusinessCheckException
     * @return
     */
    OpenGiftDto getOpenGiftDetail(Integer id) throws BusinessCheckException;

    /**
     * 根据ID删除开卡赠礼
     *
     * @param id       ID
     * @param operator 操作人
     * @throws BusinessCheckException
     * @return
     */
    void deleteOpenGift(Integer id, String operator) throws BusinessCheckException;

    /**
     * 更新开卡赠礼
     *
     * @param reqDto
     * @throws BusinessCheckException
     * @return
     * */
    MtOpenGift updateOpenGift(MtOpenGift reqDto) throws BusinessCheckException;

    /**
     * 开卡赠礼
     *
     * @param userId 会员ID
     * @param gradeId 会员等级
     * @param isNewMember 是否新会员
     * @throws BusinessCheckException
     * @return
     * */
    Boolean openGift(Integer userId, Integer gradeId, boolean isNewMember) throws BusinessCheckException;
}
