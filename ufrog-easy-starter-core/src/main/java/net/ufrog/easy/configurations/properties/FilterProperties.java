package net.ufrog.easy.configurations.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 过滤器参数
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Getter
@Setter
@ConfigurationProperties("easy.filter")
public class FilterProperties {

    /** 跨域请求 */
    private Cors cors = new Cors();

    /**
     * 跨域请求
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.5.3, 2025-11-10
     * @since 3.5.3
     */
    @Getter
    @Setter
    public static class Cors {

        /** 是否启用 */
        private boolean enabled = false;

        /** 允许来源列表 */
        private List<String> allowedOrigins = Collections.singletonList("http://localhost");

        /** 允许来源提供者 */
        private String allowedOriginProvider;

        /** 允许方法列表 */
        private List<String> allowedMethods = Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");

        /** 最大生命周期 */
        private long maxAge = 18000;
    }
}
