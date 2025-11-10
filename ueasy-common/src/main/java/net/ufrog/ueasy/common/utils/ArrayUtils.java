package net.ufrog.ueasy.common.utils;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 1.0.0, 2025-07-24
 * @since 1.0.0
 */
public class ArrayUtils {

    private ArrayUtils() {}

    public static <T> boolean isEmpty(final T[] arr) {
        return arr == null || arr.length == 0;
    }

    public static <T> T[] getOrDefault(final T[] arr, final T[] defaultValue) {
        return isEmpty(arr) ? defaultValue : arr;
    }
}
