package net.ufrog.easy.configurations.properties;

import lombok.Getter;
import lombok.Setter;
import net.ufrog.easy.log.RequestLogProcessor;
import net.ufrog.easy.log.SimpleRequestLogProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 请求日志参数
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Getter
@Setter
@ConfigurationProperties("easy.request-log")
public class RequestLogProperties {

    /** 是否启用 */
    private boolean enabled = false;

    /** 请求方法 */
    private String[] methods = {"POST", "PUT", "DELETE"};

    /** 处理实现 */
    private Class<? extends RequestLogProcessor> processor = SimpleRequestLogProcessor.class;
}
