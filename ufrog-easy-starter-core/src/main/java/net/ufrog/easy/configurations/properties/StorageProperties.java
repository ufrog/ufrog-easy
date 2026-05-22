package net.ufrog.easy.configurations.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 存储参数
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2026-05-22
 * @since 3.5.3
 */
@Getter
@Setter
@ConfigurationProperties("easy.storage")
public class StorageProperties {

    /** 是否启用 */
    private boolean enabled = false;

    /** 类型 */
    private String type = "local";

    /** 本地地址 */
    private String localPath;
}
