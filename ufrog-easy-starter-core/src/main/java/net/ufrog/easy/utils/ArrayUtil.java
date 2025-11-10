package net.ufrog.easy.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数组工具
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
public class ArrayUtil {

    /** 构造函数<br>不允许外部构造 */
    private ArrayUtil() {}

    /**
     * 判断空数组
     *
     * @param array 数组
     * @return 判断结果
     * @param <T> 数组泛型
     */
    public static <T> boolean isEmpty(final T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断数组并返回值
     *
     * @param array 数组
     * @param defaultValues 默认数组
     * @return 如果数组不为空返回原数组，如果数组为空返回默认数组
     * @param <T> 数组泛型
     */
    public static <T> T[] getOrDefault(final T[] array, final T[] defaultValues) {
        return isEmpty(array) ? defaultValues : array;
    }

    /**
     * 合并数组
     *
     * @param type 数组类型
     * @param array 原始数组
     * @param elements 新数组元素
     * @return 合并后数组
     * @param <T> 数组元素泛型
     */
    @SafeVarargs
    public static <T> T[] merge(Class<T> type, T[] array, T... elements) {
        int len = (isEmpty(array) ? 0 : array.length) + (isEmpty(elements) ? 0 : elements.length);
        if (len > 0) {
            if (isEmpty(array)) {
                return elements;
            } else if (isEmpty(elements)) {
                return array;
            } else {
                T[] result = Arrays.copyOf(array, len);
                System.arraycopy(elements, 0, result, isEmpty(array) ? 0 : array.length, elements.length);
                return result;
            }
        }
        return ObjectUtil.cast(Array.newInstance(type, 0));
    }

    /**
     * 转换成数组列表
     *
     * @param elements 元素数组
     * @return 数组列表<br>若无元素传入则返回空列表
     * @param <T> 数组元素泛型
     */
    @SafeVarargs
    public static <T> List<T> toArrayList(T... elements) {
        if (!isEmpty(elements)) {
            List<T> list = new ArrayList<>(elements.length);
            list.addAll(Arrays.asList(elements));
            return list;
        }
        return new ArrayList<>();
    }

    /**
     * 转换成数组列表
     *
     * @param arrays 数组数组
     * @return 数组列表<br>若无元素传入则返回空列表
     * @param <T> 数组元素泛型
     */
    @SafeVarargs
    public static <T> List<T> toArrayList(T[]... arrays) {
        List<T> list = new ArrayList<>();
        for (T[] array : arrays) {
            if (!isEmpty(array)) {
                list.addAll(Arrays.asList(array));
            }
        }
        return list;
    }
}
