package net.ufrog.easy.configurations.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 缓存参数
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Getter
@Setter
@ConfigurationProperties("easy.cache")
public class CacheProperties {

    /** 类型 */
    private String type = "ehcache";

    /** 前缀 */
    private String prefix = "easy_";

    /** 生存时间 */
    private int timeToLive = 4 * 60 * 60;

    /** 地址 */
    private String host = "127.0.0.1";

    /** 端口 */
    private int port = 6379;

    /** 密码 */
    private String password = null;

    /** 数据库 */
    private int database = 0;
}
