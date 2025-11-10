package net.ufrog.easy.i18n;

import java.util.Locale;
import java.util.Map;

/**
 * 消息来源接口
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
public interface EasyMessageSource {

    /**
     * 读取消息
     *
     * @param key 标识
     * @param args 参数数组
     * @param locale 地区
     * @return 消息
     */
    String get(String key, Object[] args, Locale locale);

    /**
     * 读取所有消息
     *
     * @param locale 地区
     * @return 消息映射
     */
    Map<String, String> getAll(Locale locale);

    /**
     * 获取默认位置
     *
     * @return 默认位置
     */
    Locale getDefaultLocale();
}
