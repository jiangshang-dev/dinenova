package com.dine.system.vo;

import com.dine.base.LoginUser;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
@Schema(description = "用户信息")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginUserVO implements Serializable {
    private static final long serialVersionUID = 1123100231321L;

    @Schema(description = "用户信息")
    private LoginUser user;

    @Schema(description = "token信息")
    private String token;


}
