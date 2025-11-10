package net.ufrog.ueasy.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;

import java.util.Map;

/**
 * Application request
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 1.0.0, 2025-07-27
 * @since 1.0.0
 */
public class ApplicationRequest {

    public static final String METHOD_CONNECT   = "CONNECT";
    public static final String METHOD_HEAD      = "HEAD";
    public static final String METHOD_OPTIONS   = "OPTIONS";
    public static final String METHOD_GET       = "GET";
    public static final String METHOD_POST      = "POST";
    public static final String METHOD_PUT       = "PUT";
    public static final String METHOD_PATCH     = "PATCH";
    public static final String METHOD_DELETE    = "DELETE";
    public static final String METHOD_TRACE     = "TRACE";

    private static final ThreadLocal<ApplicationRequest> CURRENT = new InheritableThreadLocal<>();

    @Getter
    private final HttpServletRequest request;

    @Getter
    private final HttpServletResponse response;

    public ApplicationRequest(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public String getRequestURI() {
        return request != null ? request.getRequestURI() : null;
    }

    public Map<String, String[]> getParameterMap() {
        return request != null ? request.getParameterMap() : null;
    }
}
