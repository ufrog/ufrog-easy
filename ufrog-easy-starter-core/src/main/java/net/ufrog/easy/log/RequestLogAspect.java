package net.ufrog.easy.log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.configurations.ExceptionHandlerConfiguration;
import net.ufrog.easy.configurations.properties.RequestLogProperties;
import net.ufrog.easy.exceptions.CommonException;
import net.ufrog.easy.json.JacksonUtil;
import net.ufrog.easy.utils.StringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 请求日志切面
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Slf4j
@Aspect
public class RequestLogAspect {

    private static Map<Class<? extends Throwable>, HttpStatus> mHttpStatus;

    /** 请求日志参数 */
    private final RequestLogProperties requestLogProperties;

    /** 请求日志处理接口 */
    private final RequestLogProcessor requestLogProcessor;

    /**
     * 构造函数
     *
     * @param requestLogProperties 请求日志参数
     */
    public RequestLogAspect(RequestLogProperties requestLogProperties, RequestLogProcessor requestLogProcessor) {
        this.requestLogProperties = requestLogProperties;
        this.requestLogProcessor = requestLogProcessor;
    }

    @Around("@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Date begin = new Date();
        ServletRequestAttributes attributes = null;
        MethodSignature signature = null;
        RequestLog requestLog = null;
        boolean checked = false;

        // Initialize data
        try {
            attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (joinPoint.getSignature() instanceof MethodSignature) {
                signature = (MethodSignature) joinPoint.getSignature();
                requestLog = signature.getMethod().getAnnotation(RequestLog.class);
                String methodType = (attributes != null) ? attributes.getRequest().getMethod() : null;
                checked = (requestLog != null && requestLog.record()) || (StringUtil.in(methodType, requestLogProperties.getMethods()) && (requestLog == null || requestLog.record()));
            } else if (log.isDebugEnabled()) {
                log.debug("Signature {}.{} is not a method signature, cannot log.", joinPoint.getTarget().getClass().getName(), joinPoint.getSignature().getName());
            }
        } catch (Throwable e) {
            log.error("Cannot log request, get error values: {}", e.getMessage());
        }

        // Log proceed or just proceed
        if (checked) {
            return log(begin, attributes, signature, joinPoint, requestLog);
        } else {
            return joinPoint.proceed();
        }
    }

    /**
     * 记录日志
     *
     * @param begin 开始时间
     * @param attributes 请求参数
     * @param signature 签名
     * @param joinPoint 结合点
     * @param requestLog 请求日志注释
     * @return 处理结果
     * @throws Throwable 处理异常
     */
    private Object log(Date begin, ServletRequestAttributes attributes, MethodSignature signature, ProceedingJoinPoint joinPoint, RequestLog requestLog) throws Throwable {
        Object pre = onPre(begin, attributes, signature, joinPoint, requestLog);
        try {
            Object result = joinPoint.proceed();
            onPost(pre, attributes, null);
            return result;
        } catch (Throwable e) {
            onPost(pre, attributes, e);
            throw e;
        }
    }

    /**
     * 预处理
     *
     * @param begin 开始时间
     * @param attributes 请求参数
     * @param signature 签名
     * @param joinPoint 结合点
     * @param requestLog 请求日志注释
     * @return 预处理结果
     */
    private Object onPre(Date begin, ServletRequestAttributes attributes, MethodSignature signature, ProceedingJoinPoint joinPoint, RequestLog requestLog) {
        String uri = (attributes != null) ? attributes.getRequest().getRequestURI() : null;
        String clazz = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        String methodType = (attributes != null) ? attributes.getRequest().getMethod() : null;
        String params = removeIgnores(getParameterJson(signature, joinPoint), requestLog);
        return requestLogProcessor.onPre(begin, uri, clazz, methodName, methodType, params);
    }

    /**
     * 后处理
     *
     * @param pre 预处理结果
     * @param attributes 请求参数
     * @param e 异常
     */
    private void onPost(Object pre, ServletRequestAttributes attributes, Throwable e) {
        int statusCode = (attributes != null && attributes.getResponse() != null) ? attributes.getResponse().getStatus() : -1;
        String exception = null;

        if (e != null) {
            HttpStatus httpStatus = getHttpStatus(e);
            CommonException ex = CommonException.newInstance(e);
            statusCode = httpStatus.value();
            exception = ex.getMessage();
        }
        requestLogProcessor.onPost(pre, new Date(), statusCode, exception);
    }

    /**
     * 获取参数字符串
     *
     * @param signature 签名
     * @param joinPoint 结合点
     * @return 参数字符串
     */
    private String getParameterJson(MethodSignature signature, ProceedingJoinPoint joinPoint) {
        if (signature != null) {
            String[] names = signature.getParameterNames();
            Object[] args = joinPoint.getArgs();
            Map<String, Object> params = new LinkedHashMap<>();

            for (int i = 0; i < names.length; i++) {
                if (i < args.length && !(args[i] instanceof MultipartFile)) {
                    if (args[i] instanceof HttpServletRequest request) {
                        params.put("_" + names[i], new HashMap<>(request.getParameterMap()));
                    } else {
                        params.put(names[i], args[i]);
                    }
                }
            }
            return JacksonUtil.toString(params);
        } else {
            return JacksonUtil.toString(joinPoint.getArgs());
        }
    }

    /**
     * 移除所有忽略字段
     *
     * @param json 数据字符串
     * @param requestLog 请求日志注释
     * @return 处理后数据字符串
     */
    private String removeIgnores(String json, RequestLog requestLog) {
        if (requestLog != null && requestLog.ignores().length > 0) {
            JsonNode node = JacksonUtil.toJsonNode(json);
            for (String ignore: requestLog.ignores()) removeIgnore(node, ignore);
            return node.toString();
        }
        return json;
    }

    /**
     * 移除忽略字段
     *
     * @param node 节点
     * @param ignore 忽略字段
     */
    private void removeIgnore(final JsonNode node, String ignore) {
        if (node.isArray()) {
            for (JsonNode n : node) {
                removeIgnore(n, ignore);
            }
        } else {
            int len = ignore.indexOf(".");
            if (len > 0) {
                String key = ignore.substring(0, len);
                String others = ignore.substring(len + 1);
                Object obj = node.get(key);

                if (obj instanceof JsonNode n) {
                    removeIgnore(n, others);
                }
            } else if (node instanceof ObjectNode n) {
                n.without(ignore);
            }
        }
    }

    /**
     * 获取响应状态
     *
     * @param e 异常
     * @return 响应状态
     */
    private HttpStatus getHttpStatus(Throwable e) {
        if (mHttpStatus == null) {
            log.info("Initializing http status...");
            mHttpStatus = new HashMap<>();
            for (Method method : ExceptionHandlerConfiguration.class.getDeclaredMethods()) {
                ExceptionHandler exceptionHandler = method.getAnnotation(ExceptionHandler.class);
                ResponseStatus responseStatus = method.getAnnotation(ResponseStatus.class);
                if (exceptionHandler != null) {
                    for (Class<? extends Throwable> exceptionClass: exceptionHandler.value()) {
                        HttpStatus httpStatus = (responseStatus == null) ? HttpStatus.INTERNAL_SERVER_ERROR : responseStatus.value();
                        log.info("Throwable {} mapping to http status: {}.", exceptionClass.getName(), httpStatus);
                        mHttpStatus.put(exceptionClass, httpStatus);
                    }
                }
            }
            log.info("Http status initialized.");
        }
        return mHttpStatus.getOrDefault(e.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
