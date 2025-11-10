package net.ufrog.easy.configurations.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 文档参数
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Getter
@Setter
@ConfigurationProperties("easy.swagger")
public class SwaggerProperties {

    /** 作者 */
    private String author = null;

    /** 作者邮箱 */
    private String email = null;

    /** 作者主页 */
    private String url = null;

    /** 标题 */
    private String title = "Swagger API";

    /** 说明 */
    private String description = "Swagger API";

    /** 版本 */
    private String version = "v1.0";
}
