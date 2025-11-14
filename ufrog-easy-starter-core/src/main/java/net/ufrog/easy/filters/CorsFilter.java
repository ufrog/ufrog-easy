package net.ufrog.easy.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.ApplicationRequest;
import net.ufrog.easy.utils.StringUtil;

import java.io.IOException;
import java.util.List;

/**
 * 跨域请求过滤器
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-12
 * @since 3.5.3
 */
@Slf4j
public class CorsFilter implements Filter {

    /** 允许来源供应器 */
    private final AllowOriginProvider allowOriginProvider;

    /** 允许方法 */
    private final List<String> allowedMethods;

    /** 最大生命周期 */
    private final long maxAge;

    /**
     * 构造函数
     *
     * @param allowOriginProvider 允许来源供应器
     * @param allowedMethods 允许方法
     * @param maxAge 最大生命周期
     */
    public CorsFilter(final AllowOriginProvider allowOriginProvider, List<String> allowedMethods, long maxAge) {
        this.allowOriginProvider = allowOriginProvider;
        this.allowedMethods = allowedMethods;
        this.maxAge = maxAge;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;
        String origin = allowOriginProvider.getAllowOrigin(req);

        if (origin == null) {
            log.warn("Cannot get allowed origin form list.");
        } else {
            resp.addHeader("Access-Control-Allow-Origin", origin);
            resp.addHeader("Access-Control-Allow-Methods", String.join(",", allowedMethods));
            resp.addHeader("Access-Control-Allow-Credentials", "true");
            resp.addHeader("Access-Control-Max-Age", maxAge + "L");
            resp.addHeader("Access-Control-Allow-Headers", req.getHeader("Access-Control-Request-Headers"));

            if (StringUtil.equals(ApplicationRequest.METHOD_OPTIONS, req.getMethod())) {
                resp.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            chain.doFilter(request, response);
        }
    }

    /**
     * 允许来源供应器
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.2.4, 2024-04-08
     * @since 3.2.4
     */
    public interface AllowOriginProvider {

        /**
         * 读取允许来源
         *
         * @param request 请求
         * @return 允许来源
         */
        String getAllowOrigin(HttpServletRequest request);
    }

    /**
     * 简单允许来源供应器
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.2.4, 2024-04-08
     * @since 3.2.4
     */
    public static class SimpleAllowOriginProvider implements AllowOriginProvider {

        /** 允许来源列表 */
        private final List<String> allowedOrigins;

        /**
         * 构造函数
         *
         * @param allowsOrigins 允许来源列表
         */
        public SimpleAllowOriginProvider(List<String> allowsOrigins) {
            this.allowedOrigins = allowsOrigins;
        }

        @Override
        public String getAllowOrigin(HttpServletRequest request) {
            String origin = request.getHeader("Origin");
            if (allowedOrigins.size() == 1 && allowedOrigins.contains("*")) {
                return "*";
            } else if (allowedOrigins.contains(origin)) {
                return origin;
            } else {
                return null;
            }
        }
    }
}
