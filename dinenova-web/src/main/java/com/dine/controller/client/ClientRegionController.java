package com.dine.controller.client;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.dto.RegionDto;
import com.dine.dto.UserInfo;
import com.dine.util.TokenUtil;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.mapper.MtRegionMapper;
import com.dine.repository.model.MtRegion;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 省/市/区controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "会员端-省/市/区相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/region")
public class ClientRegionController extends BaseController {

    private MtRegionMapper mtRegionMapper;

    /**
     * 获取地区树状结构
     */
    @Operation(summary = "获取地区树状结构")
    @Debounce
    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject tree(HttpServletRequest request) {
        String token = request.getHeader("Access-Token");
        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        if (userInfo == null) {
            return getFailureResult(1001, "用户未登录");
        }

        Map<String, Object> params = new HashMap<>();
        List<MtRegion> regionList = mtRegionMapper.selectByMap(params);

        List<RegionDto> treeData = new ArrayList<>();

        for (MtRegion mtRegion : regionList) {
             if (mtRegion.getLevel().equals(1)) {
                 RegionDto dto = new RegionDto();
                 dto.setId(mtRegion.getId());
                 dto.setName(mtRegion.getName());
                 dto.setCode(mtRegion.getCode());
                 dto.setPid(mtRegion.getPid());
                 dto.setLevel(mtRegion.getLevel() + "");
                 dto.setCity(new ArrayList<>());
                 treeData.add(dto);
             }
        }

        for (int i = 0; i < treeData.size(); i++) {
            List<RegionDto> cityArr = new ArrayList<>();
            for (MtRegion mtRegion : regionList) {
                if (treeData.get(i).getId().equals(mtRegion.getPid())) {
                    RegionDto dto = new RegionDto();
                    dto.setId(mtRegion.getId());
                    dto.setName(mtRegion.getName());
                    dto.setCode(mtRegion.getCode());
                    dto.setPid(mtRegion.getPid());
                    dto.setLevel(mtRegion.getLevel() + "");
                    List<RegionDto> regionArr = new ArrayList<>();
                    for (MtRegion mtRegion1 : regionList) {
                        if (mtRegion.getId().equals(mtRegion1.getPid())) {
                            RegionDto dto1 = new RegionDto();
                            dto1.setId(mtRegion1.getId());
                            dto1.setName(mtRegion1.getName());
                            dto1.setCode(mtRegion1.getCode());
                            dto1.setPid(mtRegion1.getPid());
                            dto1.setLevel(mtRegion1.getLevel() + "");
                            regionArr.add(dto1);
                        }
                    }
                    dto.setRegion(regionArr);
                    cityArr.add(dto);
                }
            }

            treeData.get(i).setCity(cityArr);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", treeData);

        return getSuccessResult(result);
    }
}
