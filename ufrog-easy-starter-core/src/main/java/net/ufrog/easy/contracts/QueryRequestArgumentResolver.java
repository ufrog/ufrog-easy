package net.ufrog.easy.contracts;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.contracts.requests.PageQueryRequest;
import net.ufrog.easy.contracts.requests.QueryRequest;
import net.ufrog.easy.utils.ObjectUtil;
import net.ufrog.easy.utils.StringUtil;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;

/**
 * 查询请求参数处理
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-12
 * @since 3.5.3
 */
@Slf4j
public class QueryRequestArgumentResolver implements HandlerMethodArgumentResolver {

    private static final ResolvableType QUERY_REQUEST_TYPE      = ResolvableType.forClass(QueryRequest.class);
    private static final ResolvableType PAGE_QUERY_REQUEST_TYPE = ResolvableType.forClass(PageQueryRequest.class);
    private static final String ARGUMENT_PAGE                   = "_page";
    private static final String ARGUMENT_SIZE                   = "_size";
    private static final String ARGUMENT_ORDER                  = "_order";

    @Override
    public boolean supportsParameter(@Nonnull MethodParameter parameter) {
        ResolvableType type = ResolvableType.forMethodParameter(parameter);
        return (QUERY_REQUEST_TYPE.isAssignableFrom(type) || PAGE_QUERY_REQUEST_TYPE.isAssignableFrom(type));
    }

    @Override
    public Object resolveArgument(@Nonnull MethodParameter parameter, ModelAndViewContainer mavContainer, @Nonnull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Map<String, String[]> parameterMap = webRequest.getParameterMap();
        ResolvableType type = ResolvableType.forMethodParameter(parameter);
        QueryRequest request = (QueryRequest) bindParameters(webRequest, type);
        if (PAGE_QUERY_REQUEST_TYPE.isAssignableFrom(type)) {
            return resolvePageQueryRequest(parameterMap, (PageQueryRequest) request);
        } else {
            return resolveQueryRequest(parameterMap, request);
        }
    }

    /**
     * 绑定参数
     *
     * @param webRequest 网络请求
     * @param resolvableType 可解析类型
     * @return 对象
     */
    private Object bindParameters(NativeWebRequest webRequest, ResolvableType resolvableType) {
        Object arg = ObjectUtil.newInstance(resolvableType.getRawClass());
        WebDataBinder binder = new WebDataBinder(arg);
        MutablePropertyValues mpv = new MutablePropertyValues(webRequest.getParameterMap());

        binder.bind(mpv);
        return arg;
    }

    /**
     * 处理查询请求
     *
     * @param parameterMap 参数映射
     * @param request 查询请求
     * @return 查询请求<br>与传入参数为同一个对象
     * @param <T> 请求泛型
     */
    private <T extends QueryRequest> Object resolveQueryRequest(Map<String, String[]> parameterMap, final T request) {
        if (parameterMap.containsKey(ARGUMENT_ORDER)) request.setOrder(parameterMap.get(ARGUMENT_ORDER)[0]);
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (!entry.getKey().startsWith("_")) {
                String property = entry.getKey();
                String value = entry.getValue()[0];
                String operation = "eq";

                if (property.indexOf(":") > 0) {
                    String[] parts = property.split(":");
                    property = parts[0];
                    operation = parts[1];
                }

                if (StringUtil.isEmpty(value) &&
                        !StringUtil.equals(operation, QueryRequest.Operation.IS_NULL.name(), true) &&
                        !StringUtil.equals(operation, QueryRequest.Operation.NOT_NULL.name(), true)) continue;

                QueryRequest.Criteria criteria = new QueryRequest.Criteria();
                criteria.setProperty(property);
                criteria.setOperation(QueryRequest.Operation.valueOf(operation.toUpperCase()));
                criteria.setValue(value);
                request.getCriteria().add(criteria);
            }
        }
        return request;
    }

    /**
     * 处理分页查询请求
     *
     * @param parameterMap 参数映射
     * @param request 分页查询请求
     * @return 分页查询请求<br>与传入参数为同一个对象
     * @param <T> 请求泛型
     */
    private <T extends PageQueryRequest> Object resolvePageQueryRequest(Map<String, String[]> parameterMap, final T request) {
        if (parameterMap.containsKey(ARGUMENT_PAGE)) request.setPage(Integer.parseInt(parameterMap.get(ARGUMENT_PAGE)[0]));
        if (parameterMap.containsKey(ARGUMENT_SIZE)) request.setSize(Integer.parseInt(parameterMap.get(ARGUMENT_SIZE)[0]));
        return resolveQueryRequest(parameterMap, request);
    }
}
