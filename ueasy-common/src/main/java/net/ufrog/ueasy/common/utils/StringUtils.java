package net.ufrog.ueasy.common.utils;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * String utils
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 1.0.0, 2025-07-16
 * @since 1.0.0
 */
public class StringUtils {

    /**
     * Constructor<br>
     * Doesn't allow creating new instance
     */
    private StringUtils() {}

    /**
     * Check string is empty or not
     *
     * @param str the string that needs to check
     * @return the checked result
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * If the check string is empty, return the default string
     *
     * @param str the string that needs to check
     * @param defaultStr the default string
     * @return If it is not empty, return the checked string.<br>
     *         If it is empty, return the default string
     */
    public static String getOrDefault(String str, String defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }

    /**
     * If the check string is empty, then run the supplier
     *
     * @param str the string that needs to check
     * @param supplier the supplier will be run when the string is empty
     * @return If it is not empty, return the checked string.<br>
     *         If it is empty, return supplier result
     */
    public static String getOrElse(final String str, final Supplier<String> supplier) {
        return isEmpty(str) ? supplier.get() : str;
    }

    public static void ifNotEmpty(final String str, final Consumer<String> consumer) {
        if (!isEmpty(str)) {
            consumer.accept(str);
        }
    }

    public static boolean equals(final String str, final String another, boolean caseSensitive) {
        if (caseSensitive) {
            return Objects.equals(str, another);
        } else {
            if (str != null) {
                return str.equalsIgnoreCase(another);
            } else {
                return another == null;
            }
        }
    }

    public static boolean equals(final String str, String another) {
        return equals(str, another, true);
    }

    public static boolean in(String str, String... set) {
        for (String s: set) {
            if (equals(str, s)) return true;
        }
        return false;
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    public static String random(int length, String... set) {
        Random random = new Random();
        String[] strings = ArrayUtils.getOrDefault(set, new String[] { Set.UPPERCASE, Set.LOWERCASE, Set.NUMERIC });
        StringBuffer value = new StringBuffer();
        char[] chars = String.join("", strings).toCharArray();

        IntStream.range(0, length).map(i -> random.nextInt(chars.length)).forEach(i -> value.append(chars[i]));
        return value.toString();
    }

    public static final class Set {
        public static final String NUMERIC		= "1234567890";
        public static final String LOWERCASE 	= "abcdefghijklmnopqrstuvwxyz";
        public static final String UPPERCASE 	= "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        public static final String SYMBOL		= "!@#$%^&*_+-=|:;?()";
    }
}
