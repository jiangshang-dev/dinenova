package com.dine.debounce;

public interface DebounceCache {

    /**
     * 存放数据
     *
     * @param key    键
     * @param expire 过期时间
     */
    void put(String key, long expire);

    /**
     * 获取过期时间
     *
     * @param key 键
     */
    Long get(String key);

    /**
     * 移除数据
     *
     * @param key 键
     */
    void remove(String key);

    /**
     * 是否存在
     *
     * @param key 键
     * @return 过期时间
     */
    boolean containsKey(String key);
}
