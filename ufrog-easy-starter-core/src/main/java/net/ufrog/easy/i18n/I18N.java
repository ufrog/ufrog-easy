package net.ufrog.easy.i18n;

import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.ApplicationContext;
import net.ufrog.easy.ApplicationRequest;
import net.ufrog.easy.utils.StringUtil;
import org.springframework.context.NoSuchMessageException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 国际化工具
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Slf4j
public class I18N {

    private static final String SESSION_LOCALE_KEY                  = "session.locale";
    private static final Map<String, Integer> MULTI_MESSAGE_SIZE    = new ConcurrentHashMap<>();

    /** 消息来源接口 */
    private static EasyMessageSource messageSource;

    /**
     * 读取消息
     *
     * @param key 标识
     * @param args 参数数组
     * @param locale 地区
     * @return 消息文本
     */
    public static String get(final String key, final Object[] args, final Locale locale) {
        return getFromSource(key, args, locale).map(m -> {
            if (StringUtil.equals("_multi", m)) {
                int size = getMultiMessageSize(key, locale);
                int idx = new Random().nextInt(size) + 1;
                return getFromSource(key + "." + idx, args, locale).orElse(key);
            }
            return m;
        }).orElse(key);
    }

    /**
     * 读取消息
     *
     * @param key 标识
     * @param args 参数数组
     * @return 消息文本
     */
    public static String get(final String key, final Object... args) {
        return get(key, args, getSessionLocale());
    }

    /**
     * 读取会话地区
     *
     * @return 会话地区
     */
    public static Locale getSessionLocale() {
        return ApplicationRequest.getCurrent().map(v -> v.getSession(SESSION_LOCALE_KEY, Locale.class).orElseGet(() -> {
            log.warn("There isn't a session locale available, using default.");
            Locale locale = getMessageSource().getDefaultLocale();
            setSessionLocale(locale);
            return locale;
        })).orElseGet(() -> {
            log.warn("Cannot get current application request, return default locale.");
            return getSessionLocale();
        });
    }

    /**
     * 设置会话地区
     *
     * @param locale 地区
     */
    public static void setSessionLocale(final Locale locale) {
        Optional<ApplicationRequest> optional = ApplicationRequest.getCurrent();
        if (optional.isPresent()) {
            optional.get().setSession(SESSION_LOCALE_KEY, locale);
        } else {
            log.warn("Cannot get current application and set locale failure.");
        }
    }

    /**
     * 设置会话地区
     *
     * @param localeStr 地区字符串
     */
    public static void setSessionLocale(final String localeStr) {
        setSessionLocale(parseLocale(localeStr));
    }

    /**
     * 解析地区字符串
     *
     * @param localeStr 地区字符串
     * @return 地区
     */
    public static Locale parseLocale(final String localeStr) {
        if (!StringUtil.isEmpty(localeStr) && !localeStr.contains("_") && !localeStr.contains(" ")) {
            validateLocaleStr(localeStr);
            Locale locale = Locale.forLanguageTag(localeStr);
            if (!locale.getLanguage().isEmpty()) {
                return locale;
            }
        }
        return parseLocaleStr(localeStr);
    }

    /**
     * 获取消息接口
     *
     * @return 消息接口
     */
    public static EasyMessageSource getMessageSource() {
        if (messageSource == null) messageSource = ApplicationContext.getBean(EasyMessageSource.class);
        return messageSource;
    }

    /**
     * 读取消息
     *
     * @param key 标识
     * @param args 参数数组
     * @param locale 地区
     * @return 消息
     */
    private static Optional<String> getFromSource(final String key, final Object[] args, final Locale locale) {
        try {
            return Optional.of(getMessageSource().get(key, args, locale));
        } catch (NoSuchMessageException e) {
            log.trace("Cannot find message for key {}.", key);
            return Optional.empty();
        }
    }

    /**
     * 读取多消息数量
     *
     * @param key 标识
     * @param locale 地区
     * @return 消息数量
     */
    private static int getMultiMessageSize(final String key, final Locale locale) {
        return MULTI_MESSAGE_SIZE.computeIfAbsent(key + locale.getLanguage(), k -> {
            for (int i = 1; ; i++) {
                Optional<String> optional = getFromSource(key, null, locale);
                if (optional.isEmpty()) return i - 1;
            }
        });
    }

    /**
     * 解析地区字符串
     *
     * @param localeStr 地区字符串
     * @return 地区
     */
    private static Locale parseLocaleStr(final String localeStr) {
        if (StringUtil.isEmpty(localeStr)) {
            return null;
        } else {
            //noinspection RegExpRepeatedSpace
            String delimiter = (!localeStr.contains("_") && localeStr.contains(" ")) ? " " : "_";
            String[] tokens = localeStr.split(delimiter, -1);

            if (tokens.length == 1) {
                String lang = tokens[0];
                validateLocaleStr(lang);
                return new Locale(lang);
            } else if (tokens.length == 2) {
                String lang = tokens[0];
                String country = tokens[1];
                validateLocaleStr(lang);
                validateLocaleStr(country);
                return new Locale(lang, country);
            } else if (tokens.length > 2) {
                String lang = tokens[0];
                String country = tokens[1];
                validateLocaleStr(lang);
                validateLocaleStr(country);
                String variant = Arrays.stream(tokens).skip(2).collect(Collectors.joining(delimiter));
                return new Locale(lang, country, variant);
            }
            throw new IllegalArgumentException("Invalid locale string: " + localeStr);
        }
    }

    /**
     * 验证地区字符串
     *
     * @param localeStr 地区字符串
     */
    private static void validateLocaleStr(final String localeStr) {
        for (char ch: localeStr.toCharArray()) {
            if (ch != ' ' && ch != '_' && ch != '-' && ch != '#' && !Character.isLetterOrDigit(ch)) {
                throw new IllegalArgumentException("Invalid locale string: " + localeStr);
            }
        }
    }
}
