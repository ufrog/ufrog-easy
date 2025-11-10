package net.ufrog.easy.configurations.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 参数
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Getter
@Setter
@ConfigurationProperties("easy")
public class EasyProperties {

    /** 是否生产 */
    private boolean isProduction = true;

    /** 密钥 */
    private String secret;
}
