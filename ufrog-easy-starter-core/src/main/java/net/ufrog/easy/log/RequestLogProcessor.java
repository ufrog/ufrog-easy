package net.ufrog.easy.log;

import java.util.Date;

/**
 * 请求日志处理接口
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
public interface RequestLogProcessor {

    /**
     * 预处理
     *
     * @param begin 开始时间
     * @param uri 请求地址
     * @param clazz 请求类
     * @param methodName 方法名称
     * @param methodType 方法类型
     * @param params 请求参数
     * @return 预处理反馈
     */
    Object onPre(Date begin, String uri, String clazz, String methodName, String methodType, String params);

    /**
     * 后处理
     *
     * @param obj 预处理反馈
     * @param end 结束时间
     * @param statusCode 状态代码
     * @param exception 异常消息
     */
    void onPost(Object obj, Date end, int statusCode, String exception);
}
