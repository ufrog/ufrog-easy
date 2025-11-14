package net.ufrog.easy.jpa.query;

import jakarta.persistence.*;
import net.ufrog.easy.exceptions.CommonException;
import net.ufrog.easy.templates.FreeMarkerUtil;
import net.ufrog.easy.utils.FileUtil;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 查询工具
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-13
 * @since 3.5.3
 */
public class QueryUtil {

    @PersistenceContext
    private EntityManager em;

    /**
     * 通过查询脚本查询单个实体对象
     *
     * @param queryScript 查询脚本
     * @param requiredType 请求类型
     * @return 实体对象
     * @param <T> 实体对象泛型
     */
    public <T extends Serializable> T findSingle(final QueryScript queryScript, Class<T> requiredType) {
        TypedQuery<T> query = createTypedQuery(queryScript.getScriptWithoutOrderBy(), queryScript.getParameters(), null, requiredType);
        return query.getSingleResult();
    }

    /**
     * 查询单个实体对象
     *
     * @param jpql 查询语句
     * @param parameters 参数映射
     * @param requiredType 请求类型
     * @return 实体对象
     * @param <T> 实体对象泛型
     */
    public <T extends Serializable> T findSingle(final String jpql, final Map<String, Object> parameters, Class<T> requiredType) {
        QueryScript queryScript = new QueryScript(jpql, parameters);
        return findSingle(queryScript, requiredType);
    }

    /**
     * 通过结构化脚本查询单个对象
     *
     * @param sql 结构化语言
     * @param parameters 参数映射
     * @param requiredType 请求类型
     * @return 实体对象
     * @param <T> 实体对象泛型
     */
    public <T extends Serializable> T findSingleByNative(final String sql, final Map<String, Object> parameters, Class<T> requiredType) {
        Query query = createNativeQuery(sql, parameters, null);
        unwrap(query, requiredType);
        return requiredType.cast(query.getSingleResult());
    }

    /**
     * 通过结构化脚本查询列表
     *
     * @param sql 结构化语言
     * @param parameters 参数映射
     * @param requiredType 请求类型
     * @return 实体列表
     * @param <T> 实体对象泛型
     */
    public <T extends Serializable> List<T> findByNative(final String sql, final Map<String, Object> parameters, Class<T> requiredType) {
        Query query = createNativeQuery(sql, parameters, null);
        unwrap(query, requiredType);
        //noinspection unchecked
        return query.getResultList();
    }

    /**
     * 创建指定类型查询
     *
     * @param jpql 对象查询语言
     * @param parameters 参数映射
     * @param pageable 分页信息
     * @param requiredType 请求类型
     * @return 指定类型查询
     * @param <T> 指定类型泛型
     */
    public <T extends Serializable> TypedQuery<T> createTypedQuery(final String jpql, final Map<String, Object> parameters, final Pageable pageable, final Class<T> requiredType) {
        @SuppressWarnings("SqlSourceToSinkFlow") TypedQuery<T> query = em.createQuery(jpql, requiredType);
        setParameters(query, parameters, pageable);
        return query;
    }

    /**
     * 创建结构化查询
     *
     * @param sql 结构化查询语言
     * @param parameters 参数映射
     * @param pageable 分页信息
     * @return 查询
     */
    public Query createNativeQuery(final String sql, final Map<String, Object> parameters, final Pageable pageable) {
        @SuppressWarnings("SqlSourceToSinkFlow") Query query = em.createNativeQuery(sql);
        setParameters(query, parameters, pageable);
        return query;
    }

    /**
     * 设置参数
     *
     * @param query 查询
     * @param parameters 参数映射
     * @param pageable 分页信息
     */
    public void setParameters(final Query query, final Map<String, Object> parameters, final Pageable pageable) {
        if (parameters != null && !parameters.isEmpty()) parameters.forEach(query::setParameter);
        if (pageable != null) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }
    }

    /**
     * 读取查询语言
     *
     * @param filename 文件名
     * @param parameters 参数映射
     * @return 查询语言
     */
    public String readQL(final String filename, final Map<String, Object> parameters) {
        try (InputStream inputStream = QueryUtil.class.getResourceAsStream(filename)) {
            String str = FileUtil.readAsString(inputStream, true);
            return FreeMarkerUtil.render(filename, str, parameters);
        } catch (IOException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * @param query Query
     * @param type type
     * @param <T> T
     */
    private <T extends Serializable> void unwrap(final Query query, Class<T> type) {
        Entity entity = type.getAnnotation(Entity.class);
        if (entity == null) {
            //noinspection unchecked
            query.unwrap(NativeQueryImpl.class).setTupleTransformer(new NativeQueryTupleTransformer<>(type));
        } else {
            query.unwrap(NativeQuery.class).addEntity(type);
        }
    }
}
