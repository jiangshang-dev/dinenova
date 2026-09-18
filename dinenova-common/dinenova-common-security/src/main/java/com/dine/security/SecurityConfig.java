package com.dine.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 安全中心配置
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final AuthenticationEntryPointImpl authenticationEntryPoint;

    public SecurityConfig(TokenAuthenticationFilter tokenAuthenticationFilter,
                          AuthenticationEntryPointImpl authenticationEntryPoint) {
        this.tokenAuthenticationFilter = tokenAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    /**
     * anyRequest          |   匹配所有请求路径
     * authenticated       |   用户登录后可访问
     * permitAll           |   用户可以任意访问
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/backendApi/captcha/**",
                                "/backendApi/login/**",
                                "/backendApi/userCoupon/exportList"
                        ).permitAll()
                        .requestMatchers(
                                "/clientApi/sign/**",
                                "/clientApi/page/home",
                                "/clientApi/captcha/**",
                                "/clientApi/goodsApi/**",
                                "/clientApi/coupon/list",
                                "/clientApi/coupon/detail",
                                "/clientApi/cart/**",
                                "/clientApi/user/**",
                                "/clientApi/settlement/submit",
                                "/clientApi/pay/**",
                                "/clientApi/order/todoCounts",
                                "/clientApi/store/**",
                                "/clientApi/article/**",
                                "/clientApi/message/getOne",
                                "/clientApi/message/wxPush",
                                "/clientApi/sms/sendVerifyCode",
                                "/**/system/config"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/",
                                "/static/**",
                                "/*.html",
                                "/**/*.html",
                                "/**/*.css",
                                "/**/*.js",
                                "/profile/**"
                        ).permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                "/error"
                        ).permitAll()
                        .requestMatchers("/backendApi/**", "/clientApi/**").authenticated()
                        .anyRequest().permitAll()
                )
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
