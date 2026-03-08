package net.ufrog.easy.i18n;

import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.ApplicationRequest;
import net.ufrog.easy.utils.StringUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 请求头地区来源实现
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2026-03-08
 * @since 3.5.3
 */
@Slf4j
public class HeaderLocaleSource implements EasyLocaleSource {

    private static final Map<String, Locale> mLocale = new HashMap<>();

    /** 标识 */
    private final String key;

    /**
     * 构造函数
     *
     * @param key 标识
     */
    public HeaderLocaleSource(String key) {
        this.key = key;
    }

    @Override
    public Locale getLocale() {
        return ApplicationRequest.getCurrent().map(v -> {
            String localeStr = v.getHeader(key);
            if (StringUtil.isEmpty(localeStr)) {
                return Locale.getDefault();
            } else {
                return mLocale.computeIfAbsent(localeStr.split(",")[0], I18N::parseLocale);
            }
        }).orElseGet(Locale::getDefault);
    }

    @Override
    public void setLocale(Locale locale) {
        log.debug("The method doesn't work.");
    }
}
