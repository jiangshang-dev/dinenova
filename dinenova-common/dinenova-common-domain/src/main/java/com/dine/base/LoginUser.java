package com.dine.base;

import com.dine.system.enums.UserFlagEnum;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author mrhum
 */
@Data
public class LoginUser {
    /**
     * 用户id
     */
    private Integer id;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 实名
     */
    private String userRealName;
    /**
     * 实名
     */
    private UserFlagEnum userFlag;
    /**
     * 令牌
     */
    private String token;
    /**
     * 登录地址
     */
    private String host;
    /**
     * 过期时间
     */
    private Date expire;

    /**
     * 角色信息
     */
    private List<String> roles;
}
