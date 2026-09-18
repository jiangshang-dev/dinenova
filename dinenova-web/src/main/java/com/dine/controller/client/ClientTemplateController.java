package com.dine.controller.client;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dine.service.MerchantService;
import com.dine.service.TemplateService;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.model.MtMerchant;
import com.dine.repository.model.Template;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "用户端-小程序全局配置相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/template")
public class ClientTemplateController extends BaseController {
    private TemplateService templateService;

    private MerchantService merchantService;

    @Operation(summary = "提供给小程序端渲染点餐页")
    @RequestMapping(value = "/getWxTemplate/{storeId}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getWxTemplate(HttpServletRequest request, @PathVariable("storeId") String storeId) throws BusinessCheckException {
        // LambdaQueryWrapper<MtMerchant> queryWrapper = new LambdaQueryWrapper<>();
        // queryWrapper.eq(merchantId != null, MtMerchant::getNo, merchantId);
        // Integer merchantNo = merchantService.getMerchantId(merchantId);

        LambdaQueryWrapper<Template> templateLambdaQueryWrapper = new LambdaQueryWrapper<>();
        templateLambdaQueryWrapper.eq(storeId != null, Template::getStoreId, storeId);
        Template template = templateService.getOne(templateLambdaQueryWrapper);
        return getSuccessResult(template);
    }
}
