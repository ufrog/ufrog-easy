package net.ufrog.easy.configurations;

import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.jpa.SequenceAuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 持久化自动配置
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-12
 * @since 3.5.3
 */
@Slf4j
@Configuration
@EnableJpaAuditing
public class JpaAutoConfiguration {

    @Bean
    public AuditorAware<Long> auditorAware() {
        return new SequenceAuditorAwareImpl();
    }
}
