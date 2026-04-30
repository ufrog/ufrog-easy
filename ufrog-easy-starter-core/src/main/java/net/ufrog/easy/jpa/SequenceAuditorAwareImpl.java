package net.ufrog.easy.jpa;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.ApplicationRequest;
import net.ufrog.easy.exceptions.UnauthorizedException;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * 序列审计识别实现
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-12
 * @since 3.5.3
 */
@Slf4j
public class SequenceAuditorAwareImpl implements AuditorAware<Long> {

    @Override
    @Nonnull
    public Optional<Long> getCurrentAuditor() {
        Long id = null;
        try {
            id = ApplicationRequest.getCurrentUser();
        } catch (UnauthorizedException e) {
            log.trace("Cannot get current user", e);
        }
        return Optional.of(id == null ? EasyModel.NULL : id);
    }
}
