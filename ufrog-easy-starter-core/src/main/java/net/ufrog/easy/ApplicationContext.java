package net.ufrog.easy;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.Properties;

/**
 * 应用上下文
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Slf4j
public class ApplicationContext {

    /** Springframework application context */
    private static org.springframework.context.ApplicationContext applicationContext;

    /** 应用参数 */
    private static Properties properties;

    /**
     * 初始化
     *
     * @param applicationContext Springframework application context
     */
    public static void init(org.springframework.context.ApplicationContext applicationContext) {
        // Set springframework application context
        ApplicationContext.applicationContext = applicationContext;
        log.info("Initialized application context.");
    }

    /**
     * 读取容器对象
     *
     * @param requiredType 所需类型
     * @return 容器对象
     * @param <T> 容器对象泛型
     */
    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    /**
     * 读取容器对象
     *
     * @param name 容器对象名称
     * @param requiredType 请求类型
     * @return 容器对象
     * @param <T> 容器对象泛型
     */
    public static <T> T getBean(String name, Class<T> requiredType) {
        return applicationContext.getBean(name, requiredType);
    }

    /**
     * 读取参数内容
     *
     * @param key 参数标识
     * @return 参数内容
     */
    public static Optional<String> getProperty(final String key) {
        return Optional.ofNullable(properties.getProperty(key));
    }

    /**
     * 读取参数内容
     *
     * @param key 参数标识
     * @param defaultValue 默认值
     * @return 参数内容
     */
    public static String getProperty(final String key, final String defaultValue) {
        return getProperty(key).orElse(defaultValue);
    }

    /**
     * 读取整型参数内容
     *
     * @param key 参数标识
     * @param defaultValue 默认值
     * @return 参数内容
     */
    public static int getIntProperty(final String key, final int defaultValue) {
        return getProperty(key).map(Integer::parseInt).orElse(defaultValue);
    }

    /**
     * 读取布尔参数内容
     *
     * @param key 参数标识
     * @param defaultValue 默认值
     * @return 参数内容
     */
    public static boolean getBooleanProperty(final String key, final boolean defaultValue) {
        return getProperty(key).map(Boolean::parseBoolean).orElse(defaultValue);
    }

    /**
     * 设置参数
     *
     * @param properties 参数
     */
    public static void setProperties(final Properties properties) {
        clearProperties();
        ApplicationContext.properties = properties;
    }

    /** 清除参数 */
    public static void clearProperties() {
        if (properties != null) {
            properties.clear();
            properties = null;
        }
    }
}
