package com.dine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 前端 H5 地址
 */
@Data
@ConfigurationProperties(prefix = "website")
public class WebsiteProperties {

    private String url = "";
}
