package net.ufrog.easy.caches;

import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.exceptions.CommonException;
import net.ufrog.easy.utils.NumericUtil;
import net.ufrog.easy.utils.ObjectUtil;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;

import java.io.Serializable;
import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

/**
 * EhCache 缓存实现
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Slf4j
public class EhCacheImpl implements Cache {

    private static final String ALIAS_PREFIX    = "cache_ttl_";

    private final CacheManager cacheManager;
    private final org.ehcache.Cache<String, String> keyCache;
    private final Set<String> aliases = new HashSet<>();

    /** 构造函数 */
    public EhCacheImpl() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
        keyCache = getOrCreateCache("cache_key", String.class, String.class, -1);
    }

    @Override
    public boolean safeAdd(String key, Object value, int timeToLive) {
        try {
            checkSerializable(value);
            getOrCreateCache(timeToLive).putIfAbsent(key, (Serializable) value);
            cacheKey(key, timeToLive);
            return true;
        } catch (Exception e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public void add(String key, Object value, int timeToLive) {
        safeAdd(key, value, timeToLive);
    }

    @Override
    public boolean safeSet(String key, Object value, int timeToLive) {
        try {
            checkSerializable(value);
            getOrCreateCache(timeToLive).put(key, (Serializable) value);
            cacheKey(key, timeToLive);
            return true;
        } catch (Exception e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public void set(String key, Object value, int timeToLive) {
        safeSet(key, value, timeToLive);
    }

    @Override
    public boolean safeReplace(String key, Object value, int timeToLive) {
        try {
            checkSerializable(value);
            getOrCreateCache(timeToLive).replace(key, (Serializable) value);
            cacheKey(key, timeToLive);
            return true;
        } catch (Exception e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public void replace(String key, Object value, int timeToLive) {
        safeReplace(key, value, timeToLive);
    }

    @Override
    public boolean safeRemove(String key) {
        try {
            Optional.ofNullable(keyCache.get(key)).ifPresent(alias -> {
                getOrCreateCache(alias, String.class, Serializable.class, NumericUtil.ONE).remove(key);
                keyCache.remove(key);
            });
            return true;
        } catch (Exception e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public void remove(String key) {
        safeRemove(key);
    }

    @Override
    public Optional<Object> get(String key) {
        try {
            return Optional.ofNullable(keyCache.get(key)).map(alias -> getOrCreateCache(alias, String.class, Serializable.class, NumericUtil.ONE).get(key));
        } catch (Exception e) {
            log.warn(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public synchronized long incrementAndGet(String key, int by, int timeToLive, Supplier<Long> supplier) {
        try {
            Object value = get(key).orElseGet(supplier);
            if (value instanceof Number number) {
                long val = number.longValue() + by;
                set(key, val, timeToLive);
                return val;
            }
            throw new CommonException("Cache value '" + value + "' with key '" + key + "' is not a number");
        } catch (Exception e) {
            throw CommonException.newInstance(e);
        }
    }

    @Override
    public synchronized long decrementAndGet(String key, int by, int timeToLive, Supplier<Long> supplier) {
        return incrementAndGet(key, -by, timeToLive, supplier);
    }

    @Override
    public void clear() {
        aliases.forEach(a -> getOrCreateCache(a, null, null, NumericUtil.ONE).clear());
        keyCache.clear();
    }

    /**
     * 读取或创建缓存
     *
     * @param timeToLive 生存时间
     * @return 缓存
     */
    public org.ehcache.Cache<String, Serializable> getOrCreateCache(int timeToLive) {
        return getOrCreateCache(getDefaultCacheAlias(timeToLive), String.class, Serializable.class, timeToLive);
    }

    /**
     * 读取或创建缓存
     *
     * @param alias 缓存别名
     * @param keyType 标识类型
     * @param valueType 内容类型
     * @param timeToLive 生存时间
     * @return 缓存
     * @param <K> 标识泛型
     * @param <V> 内容泛型
     */
    public <K extends Serializable, V extends Serializable> org.ehcache.Cache<K, V> getOrCreateCache(String alias, Class<K> keyType, Class<V> valueType, long timeToLive) {
        return ofNullable(cacheManager.getCache(alias, keyType, valueType)).orElseGet(() -> {
            long ttl = (timeToLive == NumericUtil.ONE && alias.startsWith(ALIAS_PREFIX)) ? Long.parseLong(alias.substring(ALIAS_PREFIX.length())) : timeToLive;
            return cacheManager.createCache(alias, newCacheConfiguration(keyType, valueType, ttl));
        });
    }

    /**
     * 读取默认缓存别名
     *
     * @param timeToLive 生存时间
     * @return 默认缓存别名
     */
    private String getDefaultCacheAlias(int timeToLive) {
        return ALIAS_PREFIX + timeToLive;
    }

    /**
     * 缓存标识
     *
     * @param key 缓存标识
     * @param timeToLive 生存时间
     */
    private void cacheKey(final String key, final int timeToLive) {
        keyCache.put(key, getDefaultCacheAlias(timeToLive));
        aliases.add(getDefaultCacheAlias(timeToLive));
    }

    /**
     * 新建缓存配置
     *
     * @param keyType 标识类型
     * @param valueType 内容类型
     * @param timeToLive 生存时间
     * @return 缓存配置
     * @param <K> 标识泛型
     * @param <V> 内容泛型
     */
    private <K extends Serializable, V extends Serializable> CacheConfiguration<K, V> newCacheConfiguration(Class<K> keyType, Class<V> valueType, long timeToLive) {
        return CacheConfigurationBuilder
                .newCacheConfigurationBuilder(keyType, valueType, ResourcePoolsBuilder.heap(2000).offheap(100, MemoryUnit.MB))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(timeToLive <= 0 ? Duration.ofNanos(Long.MAX_VALUE) : Duration.ofSeconds(timeToLive)))
                .build();
    }

    /**
     * 检查对象可否序列化
     *
     * @param obj 对象
     */
    private void checkSerializable(Object obj) {
        if (!ObjectUtil.isSerializable(obj)) throw new CommonException("Object '" + obj.getClass().getName() + "' is not serializable");
    }
}
