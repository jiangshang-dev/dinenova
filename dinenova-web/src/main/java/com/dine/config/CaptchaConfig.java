package com.dine.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 图形验证码组件配置，参数来自 application-captcha.yml
 */
@Configuration
@EnableConfigurationProperties(CaptchaProperties.class)
public class CaptchaConfig {

    @Bean
    public DefaultKaptcha defaultCaptcha(CaptchaProperties captchaProperties) {
        Properties property = new Properties();
        property.setProperty("kaptcha.border", captchaProperties.getBorder());
        property.setProperty("kaptcha.border.color", captchaProperties.getBorderColor());
        property.setProperty("kaptcha.background.clear.from", captchaProperties.getBackgroundClearFrom());
        property.setProperty("kaptcha.background.clear.to", captchaProperties.getBackgroundClearTo());
        property.setProperty("kaptcha.noise.color", captchaProperties.getNoiseColor());
        property.setProperty("kaptcha.textproducer.font.color", captchaProperties.getFontColor());
        property.setProperty("kaptcha.textproducer.font.size", String.valueOf(captchaProperties.getFontSize()));
        property.setProperty("kaptcha.textproducer.char.length", String.valueOf(captchaProperties.getCharLength()));
        property.setProperty("kaptcha.textproducer.font.names", captchaProperties.getFontNames());
        property.setProperty("kaptcha.image.width", String.valueOf(captchaProperties.getImageWidth()));
        property.setProperty("kaptcha.image.height", String.valueOf(captchaProperties.getImageHeight()));
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(new Config(property));
        return defaultKaptcha;
    }
}
