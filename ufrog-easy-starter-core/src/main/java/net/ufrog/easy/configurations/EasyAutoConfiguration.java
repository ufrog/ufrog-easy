package net.ufrog.easy.configurations;

import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.configurations.properties.*;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 自动配置
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Slf4j
@Configuration
@AutoConfigureBefore(name = {"org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration"})
@EnableConfigurationProperties({CacheProperties.class, EasyProperties.class, FilterProperties.class,
        I18NProperties.class, InterceptorProperties.class, RequestLogProperties.class})
public class EasyAutoConfiguration implements WebMvcConfigurer {
}
