package com.dine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 图形验证码，对应 application-captcha.yml
 */
@Data
@ConfigurationProperties(prefix = "kaptcha")
public class CaptchaProperties {

    private String border = "yes";

    private String borderColor = "204,204,204";

    private String backgroundClearFrom = "245,245,245";

    private String backgroundClearTo = "245,245,245";

    private String noiseColor = "195,35,97";

    private String fontColor = "195,35,97";

    private Integer fontSize = 40;

    private Integer charLength = 4;

    private String fontNames = "宋体,楷体,微软雅黑";

    private Integer imageWidth = 125;

    private Integer imageHeight = 60;
}
