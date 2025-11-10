package net.ufrog.easy.configurations.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 国际化参数
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Getter
@Setter
@ConfigurationProperties("easy.i18n")
public class I18NProperties {

    /** 类型 */
    private String type = "spring";

    /** 基础名称 */
    private String basename = "messages";

    /** 编码 */
    private String encoding = "UTF-8";
}
