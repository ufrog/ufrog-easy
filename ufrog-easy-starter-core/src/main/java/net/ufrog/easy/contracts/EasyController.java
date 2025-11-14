package net.ufrog.easy.contracts;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import lombok.Getter;
import net.ufrog.easy.contracts.requests.DataRequest;
import net.ufrog.easy.contracts.requests.PageQueryRequest;
import net.ufrog.easy.contracts.requests.QueryRequest;
import net.ufrog.easy.contracts.responses.DataResponse;
import net.ufrog.easy.contracts.responses.ListResponse;
import net.ufrog.easy.contracts.responses.PageResponse;
import net.ufrog.easy.contracts.responses.Response;
import net.ufrog.easy.exceptions.DataNotFoundException;
import net.ufrog.easy.i18n.I18N;
import net.ufrog.easy.jpa.EasyModel;
import net.ufrog.easy.jpa.EasyService;
import net.ufrog.easy.utils.ObjectUtil;
import net.ufrog.easy.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.SimpleEntityPathResolver;

import java.util.List;

/**
 * 基础控制器
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-13
 * @since 3.5.3
 */
public class EasyController<T extends EasyModel, RESP extends DataResponse, REQ extends DataRequest> implements EasyClient<RESP, REQ> {

    /** 实体类型 */
    private Class<T> entityType;

    /** 数据响应类型 */
    private Class<RESP> responseType;

    /** 数据请求类型 */
    private Class<REQ> requestType;

    /** 实体路径 */
    private EntityPath<T> entityPath;

    /** 业务接口 */
    @Getter
    private EasyService<T> service;

    /** 消息标识前缀 */
    private String messageKeyPrefix;

    @Override
    public RESP findOne(long id) {
        T entity = getService().findById(id).orElseThrow(() -> new DataNotFoundException(getEntityType(), "id", id));
        return toResponse(entity, false, null);
    }

    @Override
    public ListResponse<RESP> findList(QueryRequest request) {
        Predicate predicate = request.getPredicate(getEntityPath());
        Sort sort = request.getSort();
        List<T> list = getService().findAll(predicate, sort);
        List<RESP> responses = list.stream().map(v -> toResponse(v, true, null)).toList();
        return new ListResponse<>(responses);
    }

    @Override
    public PageResponse<RESP> findPage(PageQueryRequest request) {
        Predicate predicate = request.getPredicate(getEntityPath());
        Pageable pageable = request.getPageable();
        Page<T> page = getService().findAll(predicate, pageable);
        List<RESP> responses = page.getContent().stream().map(v -> toResponse(v, true, null)).toList();
        return new PageResponse<>(page, responses);
    }

    @Override
    public RESP create(REQ request) {
        T entity = fromRequest(request);
        return toResponse(getService().save(entity), false, getMessageKeyPrefix() + ".create.success");
    }

    @Override
    public RESP update(long id, REQ request) {
        T entity = fromRequest(request);
        return toResponse(getService().update(id, entity), false, getMessageKeyPrefix() + ".update.success");
    }

    @Override
    public Response delete(long id) {
        Response resp = new Response();
        getService().logicalDeleteById(id);
        resp.getHeader().setMessage(I18N.get(getMessageKeyPrefix() + ".delete.success"));
        return resp;
    }

    /**
     * 从实体转换成数据响应
     *
     * @param entity 实体
     * @param internal 是否内部响应
     * @param messageKey 消息标识
     * @return 数据响应
     */
    public RESP toResponse(T entity, boolean internal, String messageKey) {
        RESP response = ObjectUtil.newInstance(getResponseType()).build(entity, internal);
        StringUtil.ifNotEmpty(messageKey, v -> response.getHeader().setMessage(I18N.get(v)));
        return response;
    }

    /**
     * 从请求转换成实体数据
     *
     * @param request 数据请求
     * @return 实体数据
     */
    public T fromRequest(REQ request) {
        return request.toBean(getEntityType());
    }

    /**
     * 读取实体类型
     *
     * @return 实体类型
     */
    public Class<T> getEntityType() {
        if (entityType == null) {
            setTypes();
        }
        return entityType;
    }

    /**
     * 读取响应类型
     *
     * @return 响应类型
     */
    public Class<RESP> getResponseType() {
        if (responseType == null) {
            setTypes();
        }
        return responseType;
    }

    /**
     * 读取请求类型
     *
     * @return 请求类型
     */
    public Class<REQ> getRequestType() {
        if (requestType == null) {
            setTypes();
        }
        return requestType;
    }

    /**
     * 读取实体路径
     *
     * @return 实体路径
     */
    public EntityPath<T> getEntityPath() {
        if (entityPath == null) {
            entityPath = SimpleEntityPathResolver.INSTANCE.createPath(getEntityType());
        }
        return entityPath;
    }

    /**
     * 设置业务接口
     *
     * @param service 业务接口
     */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public void setService(EasyService<T> service) {
        this.service = service;
    }

    /**
     * 读取消息标识前缀
     *
     * @return 消息标识前缀
     */
    public String getMessageKeyPrefix() {
        if (StringUtil.isEmpty(messageKeyPrefix)) {
            char[] chars = getEntityType().getSimpleName().toCharArray();
            StringBuilder builder = new StringBuilder(chars.length * 2);
            for (int i = 0; i < chars.length; i++) {
                if (Character.isUpperCase(chars[i]) && i > 0) {
                    builder.append("-").append(chars[i]);
                } else {
                    builder.append(Character.toLowerCase(chars[i]));
                }
            }
            messageKeyPrefix = builder.toString();
        }
        return messageKeyPrefix;
    }

    /** 解析并设置各类型 */
    private void setTypes() {
        Class<?>[] classes = ObjectUtil.getGenericTypes(this.getClass());
        entityType = ObjectUtil.cast(classes[0]);
        responseType = ObjectUtil.cast(classes[1]);
        requestType = ObjectUtil.cast(classes[2]);
    }
}
