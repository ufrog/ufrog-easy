package net.ufrog.easy.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import net.ufrog.easy.exceptions.CommonException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Jackson 工具
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
public class JacksonUtil {

    /** Jackson object mapper */
    private static final ObjectMapper MAPPER;

    // Set properties
    static {
        MAPPER = new ObjectMapper();
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    /**
     * 序列化成字符串
     *
     * @param value 对象数据
     * @return 序列化字符串
     */
    public static String toString(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 反序列化
     *
     * @param value 字符串
     * @return 数据节点
     */
    public static JsonNode toJsonNode(String value) {
        try {
            return MAPPER.readTree(value);
        } catch (JsonProcessingException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 反序列化
     *
     * @param value 字节数组
     * @return 数据节点
     */
    public static JsonNode toJsonNode(byte[] value) {
        try {
            return MAPPER.readTree(value);
        } catch (IOException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 反序列化成对象数据
     *
     * @param value 字符串
     * @param type 反序列化类型
     * @return 对象数据
     * @param <T> 对象泛型
     */
    public static <T> T toObject(String value, Class<T> type) {
        try {
            return MAPPER.readValue(value, type);
        } catch (JsonProcessingException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 反序列化成对象数据
     *
     * @param value 字节数组
     * @param type 反序列化类型
     * @return 对象数据
     * @param <T> 对象泛型
     */
    public static <T> T toObject(byte[] value, Class<T> type) {
        try {
            return MAPPER.readValue(value, type);
        } catch (IOException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 反序列化成泛型对象数据
     *
     * @param value 字符串
     * @param type 反序列化类型
     * @param genericTypes 泛型数组
     * @return 对象数据
     * @param <T> 对象泛型
     */
    public static <T> T toGenericObject(String value, Class<T> type, Class<?>... genericTypes) {
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(type, genericTypes);
            return MAPPER.readValue(value, javaType);
        } catch (JsonProcessingException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 反序列化成泛型对象数据
     *
     * @param value 字节数组
     * @param type 反序列化类型
     * @param genericTypes 泛型数组
     * @return 对象数据
     * @param <T> 对象泛型
     */
    public static <T> T toGenericObject(byte[] value, Class<T> type, Class<?>... genericTypes) {
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(type, genericTypes);
            return MAPPER.readValue(value, javaType);
        } catch (IOException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 反序列化成列表
     *
     * @param value 字符串
     * @param type 列表类型
     * @return 列表数据
     * @param <T> 列表泛型
     */
    public static <T> List<T> toList(String value, Class<T> type) {
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, type);
            return MAPPER.readValue(value, javaType);
        } catch (JsonProcessingException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 反序列化成列表
     *
     * @param value 字节数组
     * @param type 列表类型
     * @return 列表数据
     * @param <T> 列表泛型
     */
    public static <T> List<T> toList(byte[] value, Class<T> type) {
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, type);
            return MAPPER.readValue(value, javaType);
        } catch (IOException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 反序列化成映射表
     *
     * @param value 字符串
     * @param keyType 标识类型
     * @param valueType 内容类型
     * @return 映射表数据
     * @param <K> 标识泛型
     * @param <V> 内容泛型
     */
    public static <K, V> Map<K, V> toMap(String value, Class<K> keyType, Class<V> valueType) {
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructMapType(Map.class, keyType, valueType);
            return MAPPER.readValue(value, javaType);
        } catch (JsonProcessingException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 反序列化成映射表
     *
     * @param value 字节数组
     * @param keyType 标识类型
     * @param valueType 内容类型
     * @return 映射表数据
     * @param <K> 标识泛型
     * @param <V> 内容泛型
     */
    public static <K, V> Map<K, V> toMap(byte[] value, Class<K> keyType, Class<V> valueType) {
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructMapType(Map.class, keyType, valueType);
            return MAPPER.readValue(value, javaType);
        } catch (IOException e) {
            throw CommonException.newInstance(e);
        }
    }
}
