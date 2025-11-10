package net.ufrog.easy.utils;

import net.ufrog.easy.exceptions.CommonException;

import java.io.*;
import java.net.URLConnection;

/**
 * 文件工具
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
public class FileUtil {

    /** 构造函数<br>不允许外部构造 */
    private FileUtil() {}

    /**
     * 读取输入流
     *
     * @param filename 文件名
     * @return 输入流
     */
    public static InputStream readAsStream(final String filename) {
        return ClassLoader.getSystemClassLoader().getResourceAsStream(filename);
    }

    /**
     * 读取文件
     *
     * @param filename 文件名
     * @param simplify 是否精简
     * @return 文件内容
     */
    public static String readAsString(final String filename, boolean simplify) {
        try (InputStream is = readAsStream(filename)) {
            return readAsString(is, simplify);
        } catch (IOException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 读取文件
     *
     * @param inputStream 输入流
     * @param simplify 是否精简
     * @return 文件内容
     */
    public static String readAsString(InputStream inputStream, boolean simplify) {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream); BufferedReader reader = new BufferedReader(inputStreamReader)) {
            StringBuilder text = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                if (simplify) {
                    text.append(line.trim()).append(" ");
                } else {
                    text.append(line).append("\n");
                }
            }
            return text.toString();
        } catch (IOException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 从文件流猜测文件媒体类型
     *
     * @param stream 文件流
     * @return 媒体类型
     */
    public static String guessMimeTypeFromStream(final InputStream stream) {
        try {
            return URLConnection.guessContentTypeFromStream(stream);
        } catch (IOException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 从文件猜测文件媒体类型
     *
     * @param file 文件
     * @return 媒体类型
     */
    public static String guessMimeTypeFromFile(final File file) {
        try (FileInputStream stream = new FileInputStream(file)) {
            return guessMimeTypeFromStream(stream);
        } catch (IOException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 从文件字节数组读取文件媒体类型
     *
     * @param bytes 文件字节数组
     * @return 媒体类型
     */
    public static String guessMimeTypeFromBytes(byte[] bytes) {
        try (ByteArrayInputStream stream = new ByteArrayInputStream(bytes)) {
            return guessMimeTypeFromStream(stream);
        } catch (IOException e) {
            throw CommonException.newInstance(e);
        }
    }
}
