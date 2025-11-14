package net.ufrog.easy.i18n;

import lombok.extern.slf4j.Slf4j;

/**
 * 国际化语言切换回调接口
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-12
 * @since 3.5.3
 */
public interface I18NChangeCallback {

    /**
     * 切换语言前回调
     *
     * @param localeStr 地区字符串
     */
    default void beforeChange(String localeStr) {}

    /**
     * 切换语言后回调
     *
     * @param localeStr 地区字符串
     */
    default void afterChange(String localeStr) {}

    /**
     * 国际化语言切换回调接口
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.4.1, 2025-02-12
     * @since 3.4.1
     */
    @Slf4j
    final class EmptyChangeCallback implements I18NChangeCallback {

        @Override
        public void beforeChange(String localeStr) {
            log.trace("Before changing localeStr {}", localeStr);
        }

        @Override
        public void afterChange(String localeStr) {
            log.trace("After Changing localeStr {}", localeStr);
        }
    }
}
