package net.ufrog.easy.interceptors;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.ufrog.easy.ApplicationRequest;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 应用拦截器
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-12
 * @since 3.5.3
 */
public class ApplicationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
        ApplicationRequest.setCurrent(new ApplicationRequest(request, response));
        return true;
    }
}
