package com.dine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.dto.ParamDto;
import com.dine.repository.model.MtSetting;
import com.dine.framework.exception.BusinessCheckException;
import java.util.List;

/**
 * 配置业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface SettingService extends IService<MtSetting> {

    /**
     * 删除配置
     *
     * @param  merchantId 商户ID
     * @param  type 类型
     * @param  name 配置名称
     * @throws BusinessCheckException
     */
    void removeSetting(Integer merchantId, String type, String name) throws BusinessCheckException;

    /**
     * 保存配置
     *
     * @param  mtSetting
     * @throws BusinessCheckException
     */
    MtSetting saveSetting(MtSetting mtSetting) throws BusinessCheckException;

    /**
     * 获取配置列表
     *
     * @param  type
     * @throws BusinessCheckException
     */
    List<MtSetting> getSettingList(Integer merchantId, String type) throws BusinessCheckException;

    /**
     * 根据配置名称获取配置信息
     *
     * @param  merchantId 商户ID
     * @param  type 类型
     * @param  name 配置名称
     * @throws BusinessCheckException
     */
    MtSetting querySettingByName(Integer merchantId, String type, String name) throws BusinessCheckException;

    /**
     * 当前存储的访问域名，不含结尾斜杠
     */
    String getUploadBasePath();

    /**
     * 拼出可访问地址。已经是 http 地址则原样返回；历史上传到本机的 /static/ 仍走本机端口。
     */
    String fileUrl(String path);

    /**
     * 获取支付方式列表
     *
     * @param platform 平台
     * @return
     * */
    List<ParamDto> getPayTypeList(String platform);

}
