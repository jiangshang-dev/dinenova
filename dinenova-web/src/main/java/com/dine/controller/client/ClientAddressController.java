package com.dine.controller.client;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.dto.AddressDto;
import com.dine.dto.UserInfo;
import com.dine.enums.StatusEnum;
import com.dine.enums.YesOrNoEnum;
import com.dine.param.AddressDetailParam;
import com.dine.service.AddressService;
import com.dine.util.TokenUtil;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.param.client.AddressRequest;
import com.dine.repository.mapper.MtRegionMapper;
import com.dine.repository.model.MtAddress;
import com.dine.repository.model.MtRegion;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import java.lang.reflect.InvocationTargetException;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收货地址controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "会员端-收货地址相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/address")
public class ClientAddressController extends BaseController {

    private MtRegionMapper mtRegionMapper;

    /**
     * 收货地址服务接口
     * */
    private AddressService addressService;

    /**
     * 保存收货地址
     */
    @Operation(summary ="保存收货地址", description ="保存会员的收货地址")
    @Debounce
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseObject save(HttpServletRequest request, @RequestBody AddressRequest address) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        String name = address.getName() == null ? "" : address.getName();
        String mobile = address.getMobile() == null ? "" : address.getMobile();
        Integer provinceId = address.getProvinceId() == null ? 0 : address.getProvinceId();
        Integer cityId = address.getCityId() == null ? 0 : address.getCityId();
        Integer regionId = address.getRegionId() == null ? 0 : address.getRegionId();
        String detail = address.getDetail() == null ? "" : address.getDetail();
        String status = address.getStatus() == null ? StatusEnum.ENABLED.getKey() : address.getStatus();
        String isDefault = address.getIsDefault() == null ? "" : address.getIsDefault();
        Integer addressId = address.getAddressId() == null ? 0 : address.getAddressId();

        if (StringUtil.isEmpty(token)) {
            return getFailureResult(1001);
        }

        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);
        if (null == mtUser) {
            return getFailureResult(1001);
        }

        MtAddress mtAddress = new MtAddress();
        mtAddress.setId(addressId);
        mtAddress.setName(name);
        mtAddress.setMobile(mobile);
        mtAddress.setProvinceId(provinceId);
        mtAddress.setCityId(cityId);
        mtAddress.setRegionId(regionId);
        mtAddress.setDetail(detail);
        mtAddress.setStatus(status);
        mtAddress.setUserId(mtUser.getId());
        mtAddress.setIsDefault(isDefault);

        addressService.saveAddress(mtAddress);
        return getSuccessResult(true);
    }

    /**
     * 获取个人收货地址列表
     */
    @Operation(summary ="获取个人收货地址列表", description ="获取个人收货地址列表")
    @Debounce
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        String token = request.getHeader("Access-Token");

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> param = new HashMap<>();

        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);
        if (null == mtUser) {
            return getFailureResult(1001);
        } else {
            param.put("userId", mtUser.getId());
        }
        param.put("status", StatusEnum.ENABLED.getKey());
        List<MtAddress> addressList = addressService.queryListByParams(param);

        List<AddressDto> dataList = new ArrayList<>();
        for (MtAddress mtAddress : addressList) {
            AddressDto address = new AddressDto();
            BeanUtils.copyProperties(mtAddress, address);

            String province = "";
            String city = "";
            String region = "";

            if (address.getProvinceId() > 0) {
                MtRegion mtProvince = mtRegionMapper.selectById(address.getProvinceId());
                if (mtProvince != null) {
                    province = mtProvince.getName();
                }
            }
            if (address.getCityId() > 0) {
                MtRegion mtCity = mtRegionMapper.selectById(address.getCityId());
                if (mtCity != null) {
                    city = mtCity.getName();
                }
            }
            if (address.getCityId() > 0) {
                MtRegion mtRegion = mtRegionMapper.selectById(address.getRegionId());
                if (mtRegion != null) {
                    region = mtRegion.getName();
                }
            }

            address.setProvinceName(province);
            address.setCityName(city);
            address.setRegionName(region);
            dataList.add(address);
        }

        result.put("list", dataList);
        return getSuccessResult(result);
    }

    /**
     * 获取收货地址详情
     */
    @Operation(summary ="获取收货地址详情", description ="根据ID获取会员收货地址详情")
    @Debounce
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    public ResponseObject detail(HttpServletRequest request, @RequestBody AddressDetailParam addressDetailParam) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        String token = request.getHeader("Access-Token") == null ? "" : request.getHeader("Access-Token");
        String addressIdStr = addressDetailParam.getAddressId() == null ? "0" : addressDetailParam.getAddressId();
        Integer addressId = 0;
        if (StringUtil.isNotEmpty(addressIdStr)) {
            addressId = Integer.parseInt(addressIdStr);
        }

        Map<String, Object> result = new HashMap<>();
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);
        if (null == mtUser || StringUtil.isEmpty(token)) {
            return getFailureResult(1001);
        }

        MtAddress mtAddress = null;
        if (addressId > 0) {
            mtAddress = addressService.detail(addressId);
        } else {
            Map<String, Object> params = new HashMap<>();
            params.put("userId", mtUser.getId());
            params.put("isDefault", YesOrNoEnum.YES.getKey());
            List<MtAddress> addressList = addressService.queryListByParams(params);
            if (addressList.size() > 0) {
                mtAddress = addressList.get(0);
            }
        }

        if (mtAddress != null) {
            if (mtAddress.getUserId().equals(mtUser.getId())) {
                AddressDto dto = new AddressDto();
                BeanUtils.copyProperties(mtAddress, dto);
                String province = "";
                String city = "";
                String region = "";
                if (dto.getProvinceId() > 0) {
                    MtRegion mtProvince = mtRegionMapper.selectById(dto.getProvinceId());
                    if (mtProvince != null) {
                        province = mtProvince.getName();
                    }
                }
                if (dto.getCityId() > 0) {
                    MtRegion mtCity = mtRegionMapper.selectById(dto.getCityId());
                    if (mtCity != null) {
                        city = mtCity.getName();
                    }
                }
                if (dto.getRegionId() > 0) {
                    MtRegion mtRegion = mtRegionMapper.selectById(dto.getRegionId());
                    if (mtRegion != null) {
                        region = mtRegion.getName();
                    }
                }

                dto.setProvinceName(province);
                dto.setCityName(city);
                dto.setRegionName(region);
                result.put("address", dto);
            }
        }

        return getSuccessResult(result);
    }
}
