package net.ufrog.easy.log;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 简单请求日志处理实现
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Slf4j
public class SimpleRequestLogProcessor implements RequestLogProcessor {

    @Override
    public Object onPre(Date begin, String uri, String clazz, String methodName, String methodType, String params) {
        log.info("Begin request uri: {}, class: {}, method name: {}, method type: {}, params: {}", uri, clazz, methodName, methodType, params);
        return null;
    }

    @Override
    public void onPost(Object obj, Date end, int statusCode, String exception) {
        log.info("End request status code: {}, exception: {}", statusCode, exception);
    }
}
