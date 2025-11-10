package net.ufrog.easy.caches;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.configurations.properties.CacheProperties;
import net.ufrog.easy.utils.DateTimeUtil;
import net.ufrog.easy.utils.StringUtil;

import java.util.*;
import java.util.function.Supplier;

/**
 * 缓存工具
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Slf4j
public class CacheUtil {

    /** 缓存 */
    private static Cache cache;

    /** 前缀 */
    private static String prefix;

    /** 生存时间 */
    private static int timeToLive;

    /**
     * 添加缓存<br>仅当缓存标识不存在时添加成功
     *
     * @param key 缓存标识
     * @param value 缓存内容
     * @param timeToLive 生存时间<br>单位：秒
     */
    public static void add(String key, Object value, int timeToLive) {
        cache.add(prefix + key, value, timeToLive);
        log.debug("Added a {}-second cache {}: {}.", timeToLive, key, value);
    }

    /**
     * 添加缓存<br>仅当缓存标识不存在时添加成功
     *
     * @param key 缓存标识
     * @param value 缓存内容
     * @param duration 生存周期
     */
    public static void add(String key, Object value, String duration) {
        add(key, value, DateTimeUtil.toSeconds(duration));
    }

    /**
     * 添加缓存<br>仅当缓存标识不存在时添加成功
     *
     * @param key 缓存标识
     * @param value 缓存内容
     */
    public static void add(String key, Object value) {
        add(key, value, timeToLive);
    }

    /**
     * 添加缓存<br>仅当缓存标识不存在时添加成功
     *
     * @param key 缓存标识
     * @param value 缓存内容
     * @param timeToLive 生存时间<br>单位：秒
     * @return 添加结果
     */
    public static boolean safeAdd(String key, Object value, int timeToLive) {
        if (cache.safeAdd(key, value, timeToLive)) {
            log.debug("Successfully added a {}-second cache {}: {}.", timeToLive, key, value);
            return true;
        } else {
            log.warn("Cannot add cache {}: {}.", key, value);
            return false;
        }
    }

    /**
     * 添加缓存<br>仅当缓存标识不存在时添加成功
     *
     * @param key 缓存标识
     * @param value 缓存内容
     * @param duration 生存周期
     * @return 添加结果
     */
    public static boolean safeAdd(String key, Object value, String duration) {
        return safeAdd(key, value, DateTimeUtil.toSeconds(duration));
    }

    /**
     * 添加缓存<br>仅当缓存标识不存在时添加成功
     *
     * @param key 缓存标识
     * @param value 缓存内容
     * @return 添加结果
     */
    public static boolean safeAdd(String key, Object value) {
        return safeAdd(key, value, timeToLive);
    }

    /**
     * 设置缓存<br>不论缓存标识是否存在都会设置为新值
     *
     * @param key 缓存标识
     * @param value 缓存内容
     * @param timeToLive 生存时间<br>单位：秒
     */
    public static void set(String key, Object value, int timeToLive) {
        cache.set(prefix + key, value, timeToLive);
        log.debug("Set a {}-second cache {}: {}.", timeToLive, key, value);
    }

    /**
     * 设置缓存<br>不论缓存标识是否存在都会设置为新值
     *
     * @param key 缓存标识
     * @param value 缓存内容
     * @param duration 生存周期
     */
    public static void set(String key, Object value, String duration) {
        set(key, value, DateTimeUtil.toSeconds(duration));
    }

    /**
     * 设置缓存<br>不论缓存标识是否存在都会设置为新值
     *
     * @param key 缓存标识
     * @param value 缓存内容
     */
    public static void set(String key, Object value) {
        set(key, value, timeToLive);
    }

    /**
     * 设置缓存<br>不论缓存标识是否存在都会设置为新值
     *
     * @param key 缓存标识
     * @param value 缓存内容
     * @param timeToLive 生存时间<br>单位：秒
     * @return 设置结果
     */
    public static boolean safeSet(String key, Object value, int timeToLive) {
        if (cache.safeSet(prefix + key, value, timeToLive)) {
            log.debug("Successfully set a {}-second cache {}: {}.", timeToLive, key, value);
            return true;
        } else {
            log.warn("Cannot set cache {}: {}.", key, value);
            return false;
        }
    }

    /**
     * 设置缓存<br>不论缓存标识是否存在都会设置为新值
     *
     * @param key 缓存标识
     * @param value 缓存内容
     * @param duration 生存周期
     * @return 设置结果
     */
    public static boolean safeSet(String key, Object value, String duration) {
        return safeSet(key, value, DateTimeUtil.toSeconds(duration));
    }

    /**
     * 设置缓存<br>不论缓存标识是否存在都会设置为新值
     *
     * @param key 缓存标识
     * @param value 缓存内容
     * @return 设置结果
     */
    public static boolean safeSet(String key, Object value) {
        return safeSet(key, value, timeToLive);
    }

    /**
     * 替换缓存<br>仅当缓存标识不存在时替换值
     *
     * @param key 缓存标识
     * @param value 缓存内容
     * @param timeToLive 生存时间<br>单位：秒
     */
    public static void replace(String key, Object value, int timeToLive) {
        cache.replace(prefix + key, value, timeToLive);
        log.debug("Replaced a {}-second cache {}: {}.", timeToLive, key, value);
    }

    /**
     * 替换缓存<br>仅当缓存标识不存在时替换值
     *
     * @param key 缓存标识
     * @param value 缓存内容
     * @param duration 生存周期
     */
    public static void replace(String key, Object value, String duration) {
        replace(key, value, DateTimeUtil.toSeconds(duration));
    }

    /**
     * 替换缓存<br>仅当缓存标识不存在时替换值
     *
     * @param key 缓存标识
     * @param value 缓存内容
     */
    public static void replace(String key, Object value) {
        replace(key, value, timeToLive);
    }

    /**
     * 替换缓存<br>仅当缓存标识不存在时替换值
     *
     * @param key 缓存标识
     * @param value 缓存内容
     * @param timeToLive 生存时间<br>单位：秒
     * @return 替换结果
     */
    public static boolean safeReplace(String key, Object value, int timeToLive) {
        if (cache.safeReplace(prefix + key, value, timeToLive)) {
            log.debug("Successfully replaced a {}-second cache {}: {}.", timeToLive, key, value);
            return true;
        } else {
            log.warn("Cannot replace cache {}: {}.", key, value);
            return false;
        }
    }

    /**
     * 替换缓存<br>仅当缓存标识不存在时替换值
     *
     * @param key 缓存标识
     * @param value 缓存内容
     * @param duration 生存周期
     * @return 替换结果
     */
    public static boolean safeReplace(String key, Object value, String duration) {
        return safeReplace(key, value, DateTimeUtil.toSeconds(duration));
    }

    /**
     * 替换缓存<br>仅当缓存标识不存在时替换值
     *
     * @param key 缓存标识
     * @param value 缓存内容
     * @return 替换结果
     */
    public static boolean safeReplace(String key, Object value) {
        return safeReplace(key, value, timeToLive);
    }

    /**
     * 移除缓存
     *
     * @param key 缓存标识
     */
    public static void remove(String key) {
        cache.remove(prefix + key);
    }

    /**
     * 移除缓存
     *
     * @param key 缓存标识
     * @return 移除结果
     */
    public static boolean safeRemove(String key) {
        return cache.safeRemove(prefix + key);
    }

    /**
     * 读取缓存
     *
     * @param key 缓存标识
     * @return 缓存内容
     */
    public static Optional<Object> get(String key) {
        return cache.get(prefix + key);
    }

    /**
     * 读取缓存
     *
     * @param key 缓存标识
     * @param requiredType 缓存内容类型
     * @param <T> 缓存内容泛型
     * @return 缓存内容
     */
    public static <T> Optional<T> get(String key, Class<T> requiredType) {
        return get(key).map(requiredType::cast);
    }

    /**
     * 读取缓存<br>如果不存在则进行计算
     *
     * @param key 缓存标识
     * @param requiredType 缓存内容类型
     * @param supplier 默认值供应
     * @return 缓存内容
     * @param <T> 缓存内容泛型
     */
    public static <T> T computeIfAbsent(String key, Class<T> requiredType, Supplier<SupplierWrapper<T>> supplier) {
        if (supplier == null) throw new NullPointerException("Supplier is null.");
        return get(key, requiredType).orElseGet(() -> {
            SupplierWrapper<T> wrapper = supplier.get();
            set(key, wrapper.getValue(), wrapper.getTimeToLive());
            return wrapper.getValue();
        });
    }

    /**
     * 读取缓存列表
     *
     * @param key 缓存标识
     * @param requiredType 列表元素类型
     * @return 列表
     * @param <T> 列表元素泛型
     */
    public static <T> Optional<List<T>> getList(String key, Class<T> requiredType) {
        return get(key).map(v -> {
            if (v instanceof Collection<?> collection) {
                List<T> list = new ArrayList<>(collection.size());
                collection.forEach(c -> list.add(requiredType.cast(c)));
                return list;
            }
            throw new ClassCastException("The value for key " + key + " is not collection.");
        });
    }

    /**
     * 读取缓存列表<br>如果不存在则进行计算
     *
     * @param key 缓存标识
     * @param requiredType 列表元素类型
     * @param supplier 默认值供应
     * @return 列表
     * @param <T> 列表元素泛型
     */
    public static <T> List<T> computeListIfAbsent(String key, Class<T> requiredType, Supplier<SupplierWrapper<List<T>>> supplier) {
        if (supplier == null) throw new NullPointerException("Supplier is null.");
        return getList(key, requiredType).orElseGet(() -> {
            SupplierWrapper<List<T>> wrapper = supplier.get();
            set(key, wrapper.getValue(), wrapper.getTimeToLive());
            return wrapper.getValue();
        });
    }

    /**
     * 读取缓存映射
     *
     * @param key 缓存标识
     * @param keyType 标识类型
     * @param valueType 内容类型
     * @return 映射
     * @param <K> 标识泛型
     * @param <V> 内容泛型
     */
    public static <K, V> Optional<Map<K, V>> getMap(String key, Class<K> keyType, Class<V> valueType) {
        return get(key).map(v -> {
            if (v instanceof Map<?,?> m) {
                Map<K, V> map = new LinkedHashMap<>();
                m.forEach((ky, vl) -> map.put(keyType.cast(ky), valueType.cast(vl)));
                return map;
            }
            throw new ClassCastException("The value for key " + key + " is not map.");
        });
    }

    /**
     * 读取缓存映射<br>如果不存在则进行计算
     *
     * @param key 缓存标识
     * @param keyType 标识类型
     * @param valueType 内容类型
     * @param supplier 默认值供应
     * @return 映射
     * @param <K> 标识泛型
     * @param <V> 内容泛型
     */
    public static <K, V> Map<K, V> computeMapIfAbsent(String key, Class<K> keyType, Class<V> valueType, Supplier<SupplierWrapper<Map<K, V>>> supplier) {
        if (supplier == null) throw new NullPointerException("Supplier is null.");
        return getMap(key, keyType, valueType).orElseGet(() -> {
            SupplierWrapper<Map<K, V>> wrapper = supplier.get();
            set(key, wrapper.getValue(), wrapper.getTimeToLive());
            return wrapper.getValue();
        });
    }

    /**
     * 递增并获取
     *
     * @param key 缓存标识
     * @param by 递增步进
     * @param timeToLive 生存时间<br>单位：秒
     * @param supplier 供应方法<br>若无值则调用方法获取起始值
     * @return 递增后数值
     */
    public static long incrementAndGet(String key, int by, int timeToLive, Supplier<Long> supplier) {
        if (supplier == null) throw new NullPointerException("Supplier is null.");
        return cache.incrementAndGet(prefix + key, by, timeToLive, supplier);
    }

    /**
     * 递增并获取
     *
     * @param key 缓存标识
     * @param by 递增步进
     * @param duration 生存周期
     * @param supplier 供应方法<br>若无值则调用方法获取起始值
     * @return 递增后数值
     */
    public static long incrementAndGet(String key, int by, String duration, Supplier<Long> supplier) {
        if (supplier == null) throw new NullPointerException("Supplier is null.");
        return incrementAndGet(key, by, DateTimeUtil.toSeconds(duration), supplier);
    }

    /**
     * 递增并获取
     *
     * @param key 缓存标识
     * @param by 递增步进
     * @param start 起始值
     * @return 递增后数值
     */
    public static long incrementAndGet(String key, int by, long start) {
        return incrementAndGet(key, by, timeToLive, () -> start);
    }

    /**
     * 递减并获取
     *
     * @param key 缓存标识
     * @param by 递减步进
     * @param timeToLive 生存时间<br>单位：秒
     * @param supplier 供应方法<br>若无值则调用方法获取起始值
     * @return 递减后数值
     */
    public static long decrementAndGet(String key, int by, int timeToLive, Supplier<Long> supplier) {
        if (supplier == null) throw new NullPointerException("Supplier is null.");
        return cache.decrementAndGet(prefix + key, by, timeToLive, supplier);
    }

    /**
     * 递减并获取
     *
     * @param key 缓存标识
     * @param by 递减步进
     * @param duration 生存周期
     * @param supplier 供应方法<br>若无值则调用方法获取起始值
     * @return 递减后数值
     */
    public static long decrementAndGet(String key, int by, String duration, Supplier<Long> supplier) {
        if (supplier == null) throw new NullPointerException("Supplier is null.");
        return decrementAndGet(key, by, DateTimeUtil.toSeconds(duration), supplier);
    }

    /**
     * 递减并获取
     *
     * @param key 缓存标识
     * @param by 递减步进
     * @param start 起始值
     * @return 递减后数值
     */
    public static long decrementAndGet(String key, int by, long start) {
        return decrementAndGet(key, by, timeToLive, () -> start);
    }

    /** 清除缓存 */
    public static void clear() {
        cache.clear();
    }

    /**
     * 初始化
     *
     * @param cacheProperties 缓存参数
     */
    public static void init(CacheProperties cacheProperties) {
        CacheUtil.prefix = cacheProperties.getPrefix();
        CacheUtil.timeToLive = cacheProperties.getTimeToLive();

        // Check cache type
        if (StringUtil.equals("ehcache", cacheProperties.getType())) {
            CacheUtil.cache = new EhCacheImpl();
            log.info("Initialized ehcache cache.");
        } else if (StringUtil.equals("redis", cacheProperties.getType())) {
            CacheUtil.cache = new RedisImpl(cacheProperties.getHost(), cacheProperties.getPort(), cacheProperties.getPassword(), cacheProperties.getDatabase());
            log.info("Initialized redis cache.");
        }
    }

    /**
     * 缓存工具
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.5.3, 2025-11-10
     * @since 3.5.3
     */
    @Getter
    public static final class SupplierWrapper<T> {

        /** 内容 */
        private final T value;

        /** 生存时间<br>单位：秒 */
        private final int timeToLive;

        /**
         * 构造函数
         *
         * @param value 数据
         * @param timeToLive 生存时间
         */
        public SupplierWrapper(T value, int timeToLive) {
            this.value = value;
            this.timeToLive = timeToLive;
        }

        /**
         * 构造函数
         *
         * @param value 数据
         */
        public SupplierWrapper(T value) {
            this(value, CacheUtil.timeToLive);
        }
    }
}
