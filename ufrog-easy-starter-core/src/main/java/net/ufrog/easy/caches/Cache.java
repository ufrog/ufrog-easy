package net.ufrog.easy.caches;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * 缓存接口
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
public interface Cache {

    /**
     * 添加缓存<br>仅当缓存标识不存在时添加成功
     *
     * @param key 缓存标识
     * @param value 缓存内容
     * @param timeToLive 生存时间<br>单位：秒
     * @return 添加结果
     */
    boolean safeAdd(final String key, final Object value, final int timeToLive);

    /**
     * 添加缓存<br>仅当缓存标识不存在时添加成功
     *
     * @param key 缓存标识
     * @param value 缓存内容
     * @param timeToLive 生存时间<br>单位：秒
     */
    void add(final String key, final Object value, final int timeToLive);

    /**
     * 设置缓存<br>不论缓存标识是否存在都会设置为新值
     *
     * @param key 缓存标识
     * @param value 缓存内容
     * @param timeToLive 生存时间<br>单位：秒
     * @return 设置结果
     */
    boolean safeSet(final String key, final Object value, final int timeToLive);

    /**
     * 设置缓存<br>不论缓存标识是否存在都会设置为新值
     *
     * @param key 缓存标识
     * @param value 缓存内容
     * @param timeToLive 生存时间<br>单位：秒
     */
    void set(final String key, final Object value, final int timeToLive);

    /**
     * 替换缓存<br>仅当缓存标识不存在时替换值
     *
     * @param key 缓存标识
     * @param value 缓存内容
     * @param timeToLive 生存时间<br>单位：秒
     * @return 替换结果
     */
    boolean safeReplace(final String key, final Object value, final int timeToLive);

    /**
     * 替换缓存<br>仅当缓存标识不存在时替换值
     *
     * @param key 缓存标识
     * @param value 缓存内容
     * @param timeToLive 生存时间<br>单位：秒
     */
    void replace(final String key, final Object value, final int timeToLive);

    /**
     * 移除缓存
     *
     * @param key 缓存标识
     * @return 移除结果
     */
    boolean safeRemove(final String key);

    /**
     * 移除缓存
     *
     * @param key 缓存标识
     */
    void remove(final String key);

    /**
     * 读取缓存
     *
     * @param key 缓存标识
     * @return 缓存内容
     */
    Optional<Object> get(final String key);

    /**
     * 递增并获取
     *
     * @param key 缓存标识
     * @param by 递增步进
     * @param timeToLive 生存时间<br>单位：秒
     * @param supplier 供应方法<br>若无值则调用方法获取起始值
     * @return 递增后数值
     */
    long incrementAndGet(final String key, final int by, final int timeToLive, Supplier<Long> supplier);

    /**
     * 递减并获取
     *
     * @param key 缓存标识
     * @param by 递减步进
     * @param timeToLive 生存时间<br>单位：秒
     * @param supplier 供应方法<br>若无值则调用方法获取起始值
     * @return 递减后数值
     */
    long decrementAndGet(final String key, final int by, final int timeToLive, Supplier<Long> supplier);

    /** 清除缓存 */
    void clear();
}
