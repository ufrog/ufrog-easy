package net.ufrog.easy.interceptors;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.ApplicationContext;
import net.ufrog.easy.caches.CacheUtil;
import net.ufrog.easy.utils.StringUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 参数加载拦截器
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Slf4j
public class PropertiesLoadInterceptor implements HandlerInterceptor {

    private static final String CACHE_KEY       = "properties_application_list";
    private static final String INSTANCE_KEY    = StringUtil.uuid();

    /** 参数加载器 */
    private final PropertiesLoader propertiesLoader;

    /**
     * 构造函数
     *
     * @param propertiesLoader 参数加载器
     */
    public PropertiesLoadInterceptor(PropertiesLoader propertiesLoader) {
        this.propertiesLoader = propertiesLoader;
    }

    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
        List<String> list = CacheUtil.getList(CACHE_KEY, String.class).orElse(new ArrayList<>());
        if (!list.contains(INSTANCE_KEY)) {
            log.info("Start caching properties for {}...", INSTANCE_KEY);
            ApplicationContext.setProperties(propertiesLoader.load());
            list.add(INSTANCE_KEY);
            CacheUtil.set(CACHE_KEY, list);
            log.info("Complete caching properties for {}...", INSTANCE_KEY);
        }
        return true;
    }

    /**
     * 参数加载器
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.5.3, 2025-11-10
     * @since 3.5.3
     */
    public interface PropertiesLoader {

        /**
         * 加载参数
         *
         * @return 参数
         */
        Properties load();
    }
}
