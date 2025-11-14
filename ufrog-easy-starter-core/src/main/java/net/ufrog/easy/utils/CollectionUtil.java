package net.ufrog.easy.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 集合工具
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-13
 * @since 3.5.3
 */
public class CollectionUtil {

    /** 构造函数<br>不允许外部构造 */
    private CollectionUtil() {}

    /**
     * 转换成数组列表
     *
     * @param iterable 迭代
     * @return 数组列表
     * @param <T> 元素泛型
     */
    public static <T> List<T> toArrayList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        if (iterable == null) {
            return list;
        } else if (iterable instanceof Collection<T> collection) {
            list.addAll(collection);
        } else {
            iterable.forEach(list::add);
        }
        return list;
    }
}
