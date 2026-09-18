package com.dine.debounce;

import cn.hutool.core.util.StrUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class DebounceCacheDefaultConfig {

    /**
     * 保存请求
     */
    private static final Map<String, Long> CACHE_MAP = new ConcurrentHashMap<>();

    @Bean
    @ConditionalOnMissingBean(value = DebounceCache.class)
    public DebounceMemoryCache debounceMemoryCache() {
        return new DebounceMemoryCache();
    }

    /**
     * 缓存默认实现
     */
    public static class DebounceMemoryCache implements DebounceCache {
        /**
         * 自动清理过期的
         */
        @Scheduled(fixedDelay = 5000)
        public void autoClearExpireKey() {
            long now = System.currentTimeMillis();
            CACHE_MAP.entrySet().removeIf(entry -> now > entry.getValue());
        }

        @Override
        public void put(String key, long expire) {
            if (StrUtil.isBlank(key)) {
                return;
            }
            CACHE_MAP.put(key, expire);
        }

        @Override
        public Long get(String key) {
            if (StrUtil.isBlank(key)) {
                return null;
            }
            return CACHE_MAP.get(key);
        }

        @Override
        public void remove(String key) {
            if (StrUtil.isBlank(key)) {
                return;
            }
            CACHE_MAP.remove(key);
        }

        @Override
        public boolean containsKey(String key) {
            if (StrUtil.isBlank(key)) {
                return false;
            }
            return CACHE_MAP.containsKey(key);
        }
    }

}
