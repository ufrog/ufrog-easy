package net.ufrog.easy.utils;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * 字符串工具
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
public class StringUtil {

    /** 构造函数<br>不允许外部构造 */
    private StringUtil() {}

    /**
     * 判断空字符串
     *
     * @param str 字符串
     * @return 判断结果
     */
    public static boolean isEmpty(final String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 判断字符串并返回值
     *
     * @param str 字符串
     * @param defaultStr 默认字符串
     * @return 如果字符串不为空返回原字符串，如果字符串为空返回默认字符串
     */
    public static String getOrDefault(final String str, final String defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }

    /**
     * 判断字符串并返回值
     *
     * @param str 字符串
     * @param supplier 字符串供应方法
     * @return 如果字符串不为空返回原字符串，如果字符串为空调用供应方法并返回结果
     */
    public static String getOrElse(final String str, final Supplier<String> supplier) {
        return isEmpty(str) ? supplier.get() : str;
    }

    /**
     * 判断字符串不为空后消费
     *
     * @param str 字符串
     * @param consumer 消费方法
     */
    public static void ifNotEmpty(final String str, final Consumer<String> consumer) {
        if (!isEmpty(str)) {
            consumer.accept(str);
        }
    }

    /**
     * 判断字符串相同
     *
     * @param one 字符串
     * @param another 字符串
     * @param ignoreCase 是否忽略大小写
     * @return 判断结果
     */
    public static boolean equals(final String one, final String another, boolean ignoreCase) {
        if (ignoreCase) {
            if (one != null) {
                return one.equalsIgnoreCase(another);
            } else {
                return another == null;
            }
        } else {
            return Objects.equals(one, another);
        }
    }

    /**
     * 判断字符串相同<br>不忽略大小写
     *
     * @param one 字符串
     * @param another 字符串
     * @return 判断结果
     */
    public static boolean equals(final String one, final String another) {
        return equals(one, another, false);
    }

    /**
     * 判断字符串是否在数组内
     *
     * @param str 字符串
     * @param array 字符串数组
     * @return 判断结果
     */
    public static boolean in(String str, String... array) {
        for (String s : array) {
            if (equals(str, s)) return true;
        }
        return false;
    }

    /**
     * 修整所有字符串字段
     *
     * @param bean 对象
     */
    public static <T> T trimAllFields(T bean) {
        ObjectUtil.getAllClassFields(bean.getClass()).forEach((k, v) -> {
            if (v.get(bean) instanceof String s) {
                v.set(bean, s.trim());
            }
        });
        return bean;
    }

    /**
     * 生成随机字符串
     *
     * @param len 字符串长度
     * @param sets 备选字串集合数组<br>不填默认为大写字母 {@link StringUtil.Set#UPPERCASE},
     *             小写字母 {@link StringUtil.Set#LOWERCASE},
     *             以及数字 {@link StringUtil.Set#NUMERIC}
     * @return 随机字符串
     */
    public static String random(final int len, String... sets) {
        Random random = new Random();
        String[] strings = ArrayUtil.getOrDefault(sets, new String[] { Set.UPPERCASE, Set.LOWERCASE, Set.NUMERIC });
        StringBuffer value = new StringBuffer(len);
        char[] chars = String.join("", strings).toCharArray();

        IntStream.range(0, len).map(i -> random.nextInt(chars.length)).forEach(i -> value.append(chars[i]));
        return value.toString();
    }

    /**
     * UUID
     *
     * @return UUID
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 字串集合
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.5.3, 2025-11-10
     * @since 3.5.3
     */
    public static final class Set {

        /** 数字 */
        public static final String NUMERIC		= "1234567890";

        /** 小写字母 */
        public static final String LOWERCASE 	= "abcdefghijklmnopqrstuvwxyz";

        /** 大写字母 */
        public static final String UPPERCASE 	= "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        /** 符号 */
        public static final String SYMBOL		= "!@#$%^&*_+-=|:;?()";
    }
}
