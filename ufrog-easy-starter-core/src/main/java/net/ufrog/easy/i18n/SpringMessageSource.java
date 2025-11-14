package net.ufrog.easy.i18n;

import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.ApplicationRequest;
import net.ufrog.easy.configurations.properties.I18NProperties;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息来源实现
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-12
 * @since 3.5.3
 */
@Slf4j
public class SpringMessageSource implements EasyMessageSource {

    private static final Map<Locale, Properties> mProperties = new ConcurrentHashMap<>();

    /** Springframework message source */
    private final ResourceBundleMessageSource messageSource;

    /** 默认语言 */
    private Properties defaultProperties;

    /**
     * 构造函数
     *
     * @param i18nProperties 国际化参数
     */
    public SpringMessageSource(I18NProperties i18nProperties) {
        messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(Arrays.stream(i18nProperties.getBasename().split(",")).map(String::trim).toArray(String[]::new));
        messageSource.setDefaultEncoding(i18nProperties.getEncoding());
    }

    @Override
    public String get(String key, Object[] args, Locale locale) {
        return messageSource.getMessage(key, args, locale);
    }

    @Override
    public Map<String, String> getAll(Locale locale) {
        Map<String, String> map = new HashMap<>();
        getProperties(null).forEach((key, value) -> map.put((String) key, (String) value));
        if (locale != null) getProperties(locale).forEach((key, value) -> map.put((String) key, (String) value));
        return map;
    }

    @Override
    public Locale getDefaultLocale() {
        return ApplicationRequest.getCurrent().map(ApplicationRequest::getLocale).orElse(null);
    }

    /**
     * 读取消息属性
     *
     * @param locale 地区
     * @return 消息属性
     */
    private Properties getProperties(final Locale locale) {
        if (locale == null) {
            if (defaultProperties == null) {
                defaultProperties = new Properties();
                load(defaultProperties, null);
            }
            return defaultProperties;
        } else {
            return mProperties.computeIfAbsent(locale, l -> {
                Properties properties = new Properties();
                load(properties, l);
                return properties;
            });
        }
    }

    /**
     * 读取消息文件
     *
     * @param properties 消息属性
     * @param locale 地区
     */
    private void load(final Properties properties, final Locale locale) {
        for (String basename: messageSource.getBasenameSet()) {
            String defaultFilename = basename + ".properties";
            String filename;

            if (locale == null) {
                filename = defaultFilename;
            } else {
                filename = defaultFilename + "_" + locale.getLanguage() + ".properties";
                File file = new File(filename);
                if (!file.exists()) {
                    log.warn("Cannot find properties file {}, read properties file {}.", filename, defaultFilename);
                    filename = defaultFilename;
                }
            }

            try (InputStream inputStream = SpringMessageSource.class.getResourceAsStream("/" + filename)) {
                if (inputStream != null) {
                    properties.load(inputStream);
                } else {
                    log.warn("Cannot find properties file {}, skip it.", filename);
                }
            } catch (IOException e) {
                log.error("Cannot read properties file {}, error message: {}, skip it.", filename, e.getMessage());
            }
        }
    }
}
