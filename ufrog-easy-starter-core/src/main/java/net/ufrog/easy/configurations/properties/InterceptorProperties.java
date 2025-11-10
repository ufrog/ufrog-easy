package net.ufrog.easy.configurations.properties;

import lombok.Getter;
import lombok.Setter;
import net.ufrog.easy.authorizes.AuthorizeFilter;
import net.ufrog.easy.interceptors.PropertiesLoadInterceptor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 拦截器参数
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Getter
@Setter
@ConfigurationProperties("easy.interceptors")
public class InterceptorProperties {

    /** 认证 */
    private Authorize authorize = new Authorize();

    /** 参数加载 */
    private PropertiesLoad propertiesLoad = new PropertiesLoad();

    /**
     * 认证
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.5.3, 2025-11-10
     * @since 3.5.3
     */
    @Getter
    @Setter
    public static class Authorize {

        /** 是否启用 */
        private boolean enabled = false;

        /** 类型 */
        private String type = "jwt";

        /** 忽略地址 */
        private String[] ignoreUris;

        /** 认证过滤器 */
        private Class<? extends AuthorizeFilter>[] filters = null;
    }

    /**
     * 参数加载
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.5.3, 2025-11-10
     * @since 3.5.3
     */
    @Getter
    @Setter
    public static class PropertiesLoad {

        /** 是否启用 */
        private boolean enabled = false;

        /** 加载器类型 */
        private Class<PropertiesLoadInterceptor.PropertiesLoader> loaderClass = null;

        /** 加载器对象名称 */
        private String loaderBeanName = null;
    }
}
