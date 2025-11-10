package net.ufrog.easy.configurations;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import net.ufrog.easy.authorizes.Authorize;
import net.ufrog.easy.configurations.properties.SwaggerProperties;
import net.ufrog.easy.utils.StringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文档自动配置
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnClass(OpenAPI.class)
public class SwaggerAutoConfiguration {

    private static final String FORMAT_SCHEME   = "JWT";

    /** 文档参数 */
    private final SwaggerProperties swaggerProperties;

    /**
     * 构造函数
     *
     * @param swaggerProperties 文档参数
     */
    public SwaggerAutoConfiguration(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    @Bean
    public OpenAPI openAPI() {
        // https://juejin.cn/post/7214015651828006967
        SecurityScheme securityScheme = new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme(Authorize.KEY_TOKEN_BEARER).bearerFormat(FORMAT_SCHEME);
        Components components = new Components().addSecuritySchemes(Authorize.KEY_AUTHORIZATION, securityScheme);
        Info info = new Info().title(swaggerProperties.getTitle()).description(swaggerProperties.getDescription()).version(swaggerProperties.getVersion());

        // Set author information
        if (!StringUtil.isEmpty(swaggerProperties.getAuthor())) {
            info.contact(new Contact().name(swaggerProperties.getAuthor()).email(swaggerProperties.getEmail()).url(swaggerProperties.getUrl()));
        }
        return new OpenAPI().components(components).info(info);
    }
}
