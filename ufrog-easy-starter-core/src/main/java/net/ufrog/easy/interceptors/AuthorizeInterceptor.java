package net.ufrog.easy.interceptors;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.ApplicationRequest;
import net.ufrog.easy.authorizes.Authorize;
import net.ufrog.easy.contracts.responses.ResponseCode;
import net.ufrog.easy.exceptions.ResponseException;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-12
 * @since 3.5.3
 */
@Slf4j
public class AuthorizeInterceptor implements HandlerInterceptor {

    /** 认证 */
    private final Authorize authorize;

    /**
     * 构造函数
     *
     * @param authorize 认证
     */
    public AuthorizeInterceptor(final Authorize authorize) {
        this.authorize = authorize;
    }

    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
        long userId = authorize.check(handler, request);
        if (userId > 0) {
            ApplicationRequest.getCurrent().ifPresent(app -> app.setUserId(userId));
            return true;
        } else if (userId == -1) {
            log.warn("Access token expired.");
        } else if (userId == -2) {
            log.warn("Access token invalidated.");
        } else if (userId == -99) {
            log.debug("This request needn't check access token.");
            return true;
        } else {
            log.warn("Unknown error occurred, returning {}.", userId);
        }
        throw new ResponseException(ResponseCode.FORBIDDEN);
    }
}
