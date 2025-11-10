package net.ufrog.easy.configurations;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.ApplicationRequest;
import net.ufrog.easy.contracts.responses.Response;
import net.ufrog.easy.contracts.responses.ResponseCode;
import net.ufrog.easy.exceptions.CommonException;
import net.ufrog.easy.exceptions.DataNotFoundException;
import net.ufrog.easy.exceptions.ResponseException;
import net.ufrog.easy.exceptions.UnauthorizedException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 异常处理配置
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandlerConfiguration implements ResponseBodyAdvice<Response> {

    @Override
    public boolean supports(@Nonnull MethodParameter returnType, @Nonnull Class<? extends HttpMessageConverter<?>> converterType) {
        return Response.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Response beforeBodyWrite(Response body, @Nonnull MethodParameter returnType, @Nonnull MediaType selectedContentType, @Nonnull Class<? extends HttpMessageConverter<?>> selectedConverterType, @Nonnull ServerHttpRequest request, @Nonnull ServerHttpResponse response) {
        if (body != null) response.setStatusCode(HttpStatusCode.valueOf(body.getHeader().getCode().getStatusCode()));
        return body;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public Response handleThrowable(Throwable e) {
        ApplicationRequest.getCurrent().ifPresentOrElse(v -> log(v, e, true), () -> log.error(e.getMessage(), e));
        return Response.newInstance(ResponseCode.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public Response handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn(e.getMessage());
        return Response.newInstance(ResponseCode.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CommonException.class)
    public Response handleCommonException(Exception e) {
        ApplicationRequest.getCurrent().ifPresentOrElse(v -> log(v, e, true), () -> log.error(e.getMessage(), e));
        return Response.newInstance(ResponseCode.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(DataNotFoundException.class)
    public Response handleDataNotFoundException(DataNotFoundException e) {
        ApplicationRequest.getCurrent().ifPresentOrElse(v -> log(v, e, false), () -> log.error(e.getMessage()));
        return Response.newInstance(ResponseCode.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public Response handleUnauthorizedException(UnauthorizedException e) {
        log.warn(e.getMessage());
        return Response.newInstance(ResponseCode.UNAUTHORIZED);
    }

    @ExceptionHandler(ResponseException.class)
    public Response handleResponseException(ResponseException e) {
        ApplicationRequest.getCurrent().ifPresentOrElse(v -> {
            v.setStatus(e.getResponseCode().getStatusCode());
            log(v, e, false);
        }, () -> log.error(e.getMessage()));
        return Response.newInstance(e.getResponseCode());
    }

    /**
     * 打印日志
     *
     * @param request 应用请求
     * @param e 异常
     * @param printStackTrace 是否打印堆栈
     */
    public void log(ApplicationRequest request, Throwable e, boolean printStackTrace) {
        if (!log.isErrorEnabled()) return;
        String message = String.format("An error occurred when %s requested %s: %s", request.getUserId(), request.getRequestURI(), e.getMessage());
        if (printStackTrace) log.error(message, e);
        else log.error(message);
    }
}
