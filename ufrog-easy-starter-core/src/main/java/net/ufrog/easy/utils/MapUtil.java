package net.ufrog.easy.utils;

import net.ufrog.easy.exceptions.CommonException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 映射表工具
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
public class MapUtil {

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> cast(Map<?, ?> map) {
        return (Map<K, V>) map;
    }

    /**
     * 哈希映射表
     *
     * @return 映射表创建器
     * @param keyType 标识类型
     * @param valueType 内容类型
     * @param <K> 标识泛型
     * @param <V> 内容泛型
     */
    @SuppressWarnings("unused")
    public static <K, V> MapBuilder<K, V> hashMap(Class<K> keyType, Class<V> valueType) {
        return new MapBuilder<>(new HashMap<>());
    }

    /**
     * 列表哈希映射表
     *
     * @return 映射表创建器
     * @param keyType 标识类型
     * @param valueType 内容类型
     * @param <K> 标识泛型
     * @param <V> 内容泛型
     */
    @SuppressWarnings("unused")
    public static <K, V> MapBuilder<K, V> linkedHashMap(Class<K> keyType, Class<V> valueType) {
        return new MapBuilder<>(new LinkedHashMap<>());
    }

    /**
     * 并发哈希映射表
     *
     * @return 映射表创建器
     * @param keyType 标识类型
     * @param valueType 内容类型
     * @param <K> 标识泛型
     * @param <V> 内容泛型
     */
    @SuppressWarnings("unused")
    public static <K, V> MapBuilder<K, V> concurrentHashMap(Class<K> keyType, Class<V> valueType) {
        return new MapBuilder<>(new ConcurrentHashMap<>());
    }

    /**
     * 放入所有内容
     *
     * @param map 映射表
     * @param objs 对象数组
     * @return 映射表
     * @param <K> 标识泛型
     * @param <V> 内容泛型
     */
    public static <K, V> Map<K, V> putAll(Map<K, V> map, Object... objs) {
        if (objs.length % 2 != 0) {
            throw new CommonException("Object array length is not even.");
        } else {
            for (int i = 0; i < objs.length; i += 2) {
                K key = ObjectUtil.cast(objs[i]);
                V val = ObjectUtil.cast(objs[i + 1]);
                map.put(key, val);
            }
            return map;
        }
    }

    /**
     * 创建映射表
     *
     * @param objs 对象数组
     * @return 哈希映射表
     * @param <K> 标识泛型
     * @param <V> 内容泛型
     */
    public static <K, V> Map<K, V> build(Object... objs) {
        return putAll(new HashMap<>(), objs);
    }

    /**
     * 映射表创建器
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.5.3, 2025-11-10
     * @since 3.5.3
     */
    public static final class MapBuilder<K, V> {

        /** 映射表 */
        private final Map<K, V> map;

        /**
         * 构造函数
         *
         * @param map 映射表
         */
        private MapBuilder(Map<K, V> map) {
            this.map = map;
        }

        /**
         * 放入内容
         *
         * @param key 标识
         * @param value 内容
         * @return 映射表创建器
         */
        public MapBuilder<K, V> put(K key, V value) {
            map.put(key, value);
            return this;
        }

        /**
         * 创建
         *
         * @return 映射表
         */
        public Map<K, V> build() {
            return map;
        }
    }
}
