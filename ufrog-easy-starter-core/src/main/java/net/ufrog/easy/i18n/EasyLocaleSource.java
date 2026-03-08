package net.ufrog.easy.i18n;

import java.util.Locale;

/**
 * 地区来源接口
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2026-03-07
 * @since 3.5.3
 */
public interface EasyLocaleSource {

    /**
     * 读取地区
     *
     * @return 地区
     */
    Locale getLocale();

    /**
     * 设置地区
     *
     * @param locale 地区
     */
    void setLocale(Locale locale);
}
