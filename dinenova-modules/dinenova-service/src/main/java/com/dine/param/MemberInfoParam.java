package com.dine.param;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import java.io.Serializable;

/**
 * 会员查询请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class MemberInfoParam extends PageParam implements Serializable {

    @Schema(description ="ID", name="id")
    private String id;

    @Schema(description ="手机号", name="mobile")
    private String mobile;

    @Schema(description ="会员号", name="userNo")
    private String userNo;

}
