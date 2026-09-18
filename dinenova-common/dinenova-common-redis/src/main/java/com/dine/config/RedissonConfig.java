package com.dine.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private String port;
    @Value("${spring.data.redis.password:}")
    private String password;

    @Bean
    public RedissonClient redissonClient() {
        // 配置
        Config config = new Config();

        SingleServerConfig singleServerConfig = config.useSingleServer().setAddress("redis://" + host + ":" + port).setConnectTimeout(6000);

        if (password != null && !password.isEmpty()) {
            singleServerConfig.setPassword(password);
        }

        // 创建RedissonClient对象
        return Redisson.create(config);
    }
}
