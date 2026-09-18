package com.dine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @program: component-app
 * @description: AppApplication
 */
@Slf4j
@EnableCaching
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class WebApplication {
    public static void main(String[] args) throws UnknownHostException {
        long startTime = System.currentTimeMillis();
        ConfigurableApplicationContext context = SpringApplication.run(WebApplication.class, args);
        ConfigurableEnvironment environment = context.getEnvironment();
        String port = environment.getProperty("server.port");
        String ip = InetAddress.getLocalHost().getHostAddress();
        log.info("====================");
        log.info("启动成功");
        log.info("接口文档:http://{}:{}/api", ip, port);
        log.info("启动耗时:{}", System.currentTimeMillis() - startTime + "ms");
        log.info("系统信息:{}", System.getProperty("os.name"));
        log.info("系统信息:{}", System.getProperty("os.arch"));
        log.info("====================");
    }
}