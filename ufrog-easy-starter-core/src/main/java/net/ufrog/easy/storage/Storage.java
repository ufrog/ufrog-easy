package net.ufrog.easy.storage;

import java.nio.charset.Charset;

/**
 * 存储接口
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2026-04-14
 * @since 3.5.3
 */
public interface Storage {

    /**
     * 读取前缀
     *
     * @return 前缀
     */
    String getPrefix();

    /**
     * 读取内容
     *
     * @param key 键值
     * @return 内容
     */
    byte[] get(String key);

    /**
     * 读取内容字符串
     *
     * @param key 键值
     * @param charset 字符集
     * @return 内容字符串
     */
    String get(String key, Charset charset);

    /**
     * 存入内容
     *
     * @param bytes 内容
     * @param originalFilename 原始文件名
     * @param filename 文件名
     * @param parent 父目录
     * @param isAutoParent 是否自动创建父目录
     * @return 键值
     */
    String put(byte[] bytes, String originalFilename, String filename, String parent, boolean isAutoParent);

    /**
     * 存入内容
     *
     * @param bytes 内容
     * @param originalFilename 原始文件名
     * @param parent 父目录
     * @param isAutoParent 是否自动创建父目录
     * @return 键值
     */
    String put(byte[] bytes, String originalFilename, String parent, boolean isAutoParent);

    /**
     * 存入内容
     *
     * @param bytes 内容
     * @return 键值
     */
    String put(byte[] bytes);

    /**
     * 替换内容
     *
     * @param key 键值
     * @param bytes 内容
     * @return 是否成功
     */
    boolean replace(String key, byte[] bytes);

    /**
     * 删除内容
     *
     * @param key 键值
     * @return 是否成功
     */
    boolean delete(String key);
}
