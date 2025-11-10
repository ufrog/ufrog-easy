package net.ufrog.easy.authorizes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.utils.StringUtil;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.method.HandlerMethod;

/**
 * 认证
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Slf4j
public abstract class Authorize {

    public static final String KEY_AUTHORIZATION   = "Authorization";
    public static final String KEY_TOKEN_BEARER    = "Bearer";

    /** 是否生产 */
    private final boolean isProduction;

    /** 忽略地址 */
    private final String[] ignoreURIs;

    /** 认证过滤器 */
    private final AuthorizeFilter[] filters;

    /**
     * 构造函数
     *
     * @param isProduction 是否生产
     * @param ignoreURIs 忽略地址
     * @param filters 认证过滤器
     */
    public Authorize(boolean isProduction, String[] ignoreURIs, AuthorizeFilter[] filters) {
        this.isProduction = isProduction;
        this.ignoreURIs = ignoreURIs;
        this.filters = filters;
    }

    /**
     * 检查认证
     *
     * @param handler 操作方法
     * @param request 网络请求
     * @return 正数：校验成功并返回用户编号<br>
     *         -1：凭证已过期<br>
     *         -2：凭证校验失败<br>
     *         -99：无需校验凭证
     */
    public long check(Object handler, HttpServletRequest request) {
        if (!ignore(handler, request)) {
            String token = request.getHeader(KEY_AUTHORIZATION);
            if (!StringUtil.isEmpty(token) && (token.startsWith(KEY_TOKEN_BEARER) || !isProduction)) {
                if (token.startsWith(KEY_TOKEN_BEARER)) token = token.substring(KEY_TOKEN_BEARER.length()).trim();
                long result = checkToken(token);
                if (result > 0 && filters != null) {
                    for (AuthorizeFilter filter : filters) {
                        if (!filter.check(token, result)) return -2;
                    }
                }
                return result;
            }
            return -2;
        }
        return -99;
    }

    /**
     * 检查访问凭证
     *
     * @param token 访问凭证
     * @return 检查结果
     */
    public abstract long checkToken(String token);

    /**
     * 获取访问凭证
     *
     * @param id 用户编号
     * @param expire 有效期，单位毫秒
     * @return 访问凭证
     */
    @SuppressWarnings("unused")
    public abstract String getToken(long id, long expire);

    /**
     * 是否忽略
     *
     * @param handler 操作方法
     * @param request 网络请求
     * @return 是否忽略
     */
    private boolean ignore(Object handler, HttpServletRequest request) {
        if (checkIgnoreURI(request)) {
            return true;
        } else if (handler instanceof HandlerMethod handlerMethod) {
            return checkIgnoreAnnotation(handlerMethod);
        } else {
            return handler instanceof CorsConfigurationSource;
        }
    }

    /**
     * 检查是否非校验地址
     *
     * @param request 网络请求
     * @return 检查结果
     */
    private boolean checkIgnoreURI(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        for (String uri: ignoreURIs) {
            if (uri.endsWith("**") && requestURI.startsWith(uri.replaceAll("/\\*\\*", ""))) {
                return true;
            } else if (StringUtil.equals(uri, requestURI)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否有忽略标注
     *
     * @param handlerMethod 操作方法
     * @return 检查结果
     */
    private boolean checkIgnoreAnnotation(HandlerMethod handlerMethod) {
        return handlerMethod.getMethod().getAnnotation(AuthorizeIgnore.class) != null;
    }
}
