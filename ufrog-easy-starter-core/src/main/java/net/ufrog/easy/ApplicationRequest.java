package net.ufrog.easy;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.exceptions.CommonException;
import net.ufrog.easy.exceptions.UnauthorizedException;
import net.ufrog.easy.utils.ArrayUtil;
import net.ufrog.easy.utils.FileUtil;
import net.ufrog.easy.utils.MapUtil;
import net.ufrog.easy.utils.StringUtil;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * 应用请求
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Slf4j
public class ApplicationRequest {

    public static final String METHOD_OPTIONS   = "OPTIONS";
    public static final String METHOD_HEAD      = "HEAD";
    public static final String METHOD_GET       = "GET";
    public static final String METHOD_POST      = "POST";
    public static final String METHOD_PUT       = "PUT";
    public static final String METHOD_PATCH     = "PATCH";
    public static final String METHOD_DELETE    = "DELETE";
    public static final String METHOD_TRACE     = "TRACE";

    private static final ThreadLocal<ApplicationRequest> CURRENT = new InheritableThreadLocal<>();

    /** 超文本协议请求 */
    private final HttpServletRequest httpServletRequest;

    /** 超文本协议响应 */
    private final HttpServletResponse httpServletResponse;

    /** 用户编号 */
    @Getter
    @Setter
    private long userId;

    /**
     * 构造函数
     *
     * @param httpServletRequest 超文本协议请求
     * @param httpServletResponse 超文本协议响应
     */
    public ApplicationRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
    }

    /**
     * 读取请求地址
     *
     * @return 请求地址
     */
    public String getRequestURI() {
        return httpServletRequest.getRequestURI();
    }

    /**
     * 读取参数映射
     *
     * @return 参数映射
     */
    public Map<String, String[]> getParameters() {
        return httpServletRequest.getParameterMap();
    }

    /**
     * 读取参数数组
     *
     * @param key 参数标识
     * @return 参数数组
     */
    public String[] getParameterValues(String key) {
        return getParameters().get(key);
    }

    /**
     * 读取参数内容
     *
     * @param key 参数标识
     * @return 参数内容
     */
    public String getParameterValue(final String key) {
        String[] values = getParameterValues(key);
        return ArrayUtil.isEmpty(values) ? null : values[0];
    }

    /**
     * 读取超文本协议会话
     *
     * @return 超文本协议会话
     */
    public HttpSession getSession() {
        return httpServletRequest.getSession();
    }

    /**
     * 读取超文本协议会话内容
     *
     * @param key 会话标识
     * @param type 会话内容类型
     * @return 会话内容
     * @param <T> 会话内容泛型
     */
    public <T> Optional<T> getSession(final String key, final Class<T> type) {
        return Optional.ofNullable(getSession().getAttribute(key)).map(type::cast);
    }

    /**
     * 设置超文本协议会话内容
     *
     * @param key 会话标识
     * @param value 会话内容
     */
    public void setSession(final String key, Object value) {
        httpServletRequest.getSession().setAttribute(key, value);
    }

    /**
     * 读取位置
     *
     * @return 如果会话中定义了位置则读取会话位置<br>
     *         如果没有定义则从请求头中读取位置
     */
    public Locale getLocale() {
        return getSession(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.class).orElse(httpServletRequest.getLocale());
    }

    /**
     * 读取文件映射
     *
     * @return 文件映射
     */
    public MultiValueMap<String, MultipartFile> getMultipartFiles() {
        if (httpServletRequest instanceof MultipartHttpServletRequest multipartHttpServletRequest) {
            return multipartHttpServletRequest.getMultiFileMap();
        }
        return new LinkedMultiValueMap<>();
    }

    /**
     * 读取文件列表
     *
     * @param key 文件标识
     * @return 文件列表
     */
    public List<MultipartFile> getMultipartFiles(String key) {
        return getMultipartFiles().get(key);
    }

    /**
     * 读取文件
     *
     * @param key 文件标识
     * @return 文件
     */
    public MultipartFile getMultipartFile(String key) {
        List<MultipartFile> multipartFiles = getMultipartFiles().get(key);
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            return multipartFiles.get(0);
        }
        return null;
    }

    /**
     * 读取远程地址
     *
     * @return 远程地址
     */
    public String getRemoteAddress() {
        String address = httpServletRequest.getHeader("X-Forwarded-For");
        if (StringUtil.isEmpty(address) || StringUtil.equals("unknown", address, true)) {
            address = httpServletRequest.getHeader("Proxy-Client-IP");
        } if (StringUtil.isEmpty(address) || StringUtil.equals("unknown", address, true)) {
            address = httpServletRequest.getHeader("WL-Proxy-Client-IP");
        } if (StringUtil.isEmpty(address) || StringUtil.equals("unknown", address, true)) {
            address = httpServletRequest.getHeader("X-Real-IP");
        } if (StringUtil.isEmpty(address) || StringUtil.equals("unknown", address, true)) {
            address = httpServletRequest.getRemoteAddr();
            if (StringUtil.equals("127.0.0.1", address) || StringUtil.equals("0:0:0:0:0:0:0:1", address)) {
                try {
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    address = inetAddress.getHostAddress();
                } catch (UnknownHostException e) {
                    throw CommonException.newInstance(e);
                }
            }
        } if (!StringUtil.isEmpty(address) && address.contains(",")) {
            int idx = address.indexOf(",");
            if (idx > 0) address = address.substring(0, idx);
        }
        return address;
    }

    /**
     * 写入响应
     *
     * @param status 状态码
     * @param bytes 字节数组
     * @param contentType 内容类型
     * @param charset 字符集
     * @param headers 头信息
     */
    public void write(int status, byte[] bytes, String contentType, Charset charset, Map<String, String> headers) {
        httpServletResponse.setStatus(status);
        httpServletResponse.setCharacterEncoding(charset.name());
        httpServletResponse.setContentType(contentType);
        httpServletResponse.setContentLength(bytes.length);
        if (headers != null) headers.forEach(httpServletResponse::setHeader);

        try (ServletOutputStream out = httpServletResponse.getOutputStream()) {
            out.write(bytes);
            out.flush();
        } catch (IOException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 下载文件<br>写入下载文件信息
     *
     * @param filename 文件名
     * @param charset 字符集
     * @param bytes 文件字节数组
     */
    public void download(String filename, Charset charset, byte[] bytes) {
        String contentType = StringUtil.getOrDefault(FileUtil.guessMimeTypeFromBytes(bytes), "text/plain");
        Map<String, String> header = MapUtil.hashMap(String.class, String.class).put("Content-Disposition", "attachment; filename=\"" + new String(filename.getBytes(), StandardCharsets.ISO_8859_1) + "\"").build();
        write(HttpStatus.OK.value(), bytes, contentType, charset, header);
    }

    /**
     * 设置响应状态代码
     *
     * @param status 响应状态代码
     */
    public void setStatus(int status) {
        httpServletResponse.setStatus(status);
    }

    /**
     * 读取当前应用请求
     *
     * @return 当前应用请求
     */
    public static Optional<ApplicationRequest> getCurrent() {
        return Optional.ofNullable(CURRENT.get());
    }

    /**
     * 获取当前用户
     *
     * @return 用户编号
     */
    public static long getCurrentUser() {
        return getCurrent().orElseThrow(UnauthorizedException::new).getUserId();
    }

    /**
     * 设置当前应用请求
     *
     * @param applicationRequest 应用请求
     */
    public static void setCurrent(ApplicationRequest applicationRequest) {
        CURRENT.set(applicationRequest);
    }
}
