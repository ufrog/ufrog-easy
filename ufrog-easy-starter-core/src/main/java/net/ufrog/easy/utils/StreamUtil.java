package net.ufrog.easy.utils;

import net.ufrog.easy.exceptions.CommonException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 流工具
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-14
 * @since 3.5.3
 */
public class StreamUtil {

    /** 构造函数<br>不允许外部构造 */
    private StreamUtil() {}

    /**
     * 复制输入流到输出流
     *
     * @param source 源输入流
     * @param dest 目标输出流
     * @return 复制字节长度
     */
    public static long copy(InputStream source, OutputStream dest) {
        byte[] bytes = new byte[4096];
        long total = 0;
        while (true) {
            try {
                int len = source.read(bytes);
                if (len == -1) break;
                dest.write(bytes, 0, len);
                total += len;
            } catch (IOException e) {
                throw CommonException.newInstance(e);
            }
        }
        return total;
    }

    /**
     * 输入流转换字节数组
     *
     * @param inputStream 输入流
     * @return 字节数组
     */
    public static byte[] toByteArray(InputStream inputStream) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(Math.max(32, inputStream.available()))) {
            copy(inputStream, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw CommonException.newInstance(e);
        }
    }
}
