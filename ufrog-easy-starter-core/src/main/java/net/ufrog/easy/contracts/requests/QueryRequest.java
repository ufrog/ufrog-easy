package net.ufrog.easy.contracts.requests;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.Getter;
import lombok.Setter;
import net.ufrog.easy.exceptions.CommonException;
import net.ufrog.easy.utils.StringUtil;
import org.springframework.data.domain.Sort;

import java.io.Serial;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询请求
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-12
 * @since 3.5.3
 */
@Getter
@Setter
public class QueryRequest extends Request {

    @Serial
    private static final long serialVersionUID = -9070525629984612320L;

    private static final Map<EntityPath<?>, Map<String, Path<?>>> ENTITY_PATH_MAP = new HashMap<>();

    /** 准则 */
    @Parameter(hidden = true)
    private List<Criteria> criteria;

    /** 排序 */
    @Parameter(name = "_order", in = ParameterIn.QUERY, description = "排序", example = "id,name:desc")
    private String order;

    /** 构造函数 */
    public QueryRequest() {
        criteria = new ArrayList<>();
    }

    /**
     * 获取排序
     *
     * @return 排序
     */
    public Sort getSort() {
        if (StringUtil.isEmpty(order)) {
            return Sort.by("id");
        } else {
            List<Sort.Order> lOrder = new ArrayList<>();
            String[] orders = order.split(",");
            for (String o : orders) {
                if (o.indexOf(":") > 0) {
                    String property = o.substring(0, o.indexOf(":"));
                    String direction = o.substring(o.indexOf(":") + 1);
                    if (StringUtil.equals("desc", direction)) {
                        lOrder.add(Sort.Order.desc(property));
                    } else {
                        lOrder.add(Sort.Order.asc(property));
                    }
                } else {
                    lOrder.add(Sort.Order.asc(o));
                }
            }
            return Sort.by(lOrder);
        }
    }

    /**
     * 读取断言
     *
     * @param root 实体路径
     * @return 断言
     */
    public Predicate getPredicate(EntityPath<?> root) {
        Map<String, Path<?>> map = getPathMap(root);
        BooleanBuilder builder = new BooleanBuilder();
        for (Criteria c : criteria) {
            Path<?> path = map.get(c.getProperty());
            if (path != null) {
                if (path instanceof StringPath stringPath) {
                    resolveStringPath(stringPath, c.getValue(), c.getOperation(), builder);
                } else if (path instanceof NumberPath<?> numberPath) {
                    Class<?> type = numberPath.getType();
                    if (type == Long.class) {
                        Long value = StringUtil.isEmpty(c.getValue()) ? null : Long.parseLong(c.getValue());
                        //noinspection unchecked
                        resolveNumberPath((NumberPath<Long>) numberPath, value, c.getOperation(), builder);
                    } else if (type == Integer.class) {
                        Integer value = StringUtil.isEmpty(c.getValue()) ? null : Integer.parseInt(c.getValue());
                        //noinspection unchecked
                        resolveNumberPath((NumberPath<Integer>) numberPath, value, c.getOperation(), builder);
                    } else {
                        throw new CommonException("Unsupported number path type: " + type);
                    }
                }
            }
        }
        return builder;
    }

    /**
     * 处理字符串路径
     *
     * @param stringPath 字符串路径
     * @param value 内容
     * @param operation 操作
     * @param builder 构建器
     */
    private void resolveStringPath(StringPath stringPath, String value, Operation operation, BooleanBuilder builder) {
        switch (operation) {
            case EQ:
                builder.and(stringPath.eq(value));
                break;
            case NE:
                builder.and(stringPath.ne(value));
                break;
            case LIKE:
                builder.and(stringPath.like(value));
                break;
            case NOT_LIKE:
                builder.and(stringPath.notLike(value));
                break;
            case CONTAINS:
                builder.and(stringPath.contains(value));
                break;
            case STARTS_WITH:
                builder.and(stringPath.startsWith(value));
                break;
            case ENDS_WITH:
                builder.and(stringPath.endsWith(value));
                break;
            case IS_NULL:
                builder.and(stringPath.isNull());
                break;
            case NOT_NULL:
                builder.and(stringPath.isNotNull());
                break;
        }
    }

    /**
     * 处理数字路径
     *
     * @param numberPath 数字路径
     * @param value 内容
     * @param operation 操作
     * @param builder 构建器
     */
    private <T extends Number & Comparable<?>> void resolveNumberPath(NumberPath<T> numberPath, T value, Operation operation, BooleanBuilder builder) {
        switch (operation) {
            case EQ:
                builder.and(numberPath.eq(value));
                break;
            case NE:
                builder.and(numberPath.ne(value));
                break;
            case GT:
                builder.and(numberPath.gt(value));
                break;
            case GOE:
                builder.and(numberPath.goe(value));
                break;
            case LT:
                builder.and(numberPath.lt(value));
                break;
            case LOE:
                builder.and(numberPath.loe(value));
                break;
        }
    }

    /**
     * 读取路径映射
     *
     * @param root 实体路径
     * @return 路径映射
     */
    private Map<String, Path<?>> getPathMap(EntityPath<?> root) {
        return ENTITY_PATH_MAP.computeIfAbsent(root, k -> {
            Map<String, Path<?>> map = new HashMap<>();
            Field[] fields = k.getClass().getDeclaredFields();
            for (Field f : fields) {
                try {
                    if (Modifier.isPublic(f.getModifiers())) {
                        Object value = f.get(root);
                        if (value instanceof Path<?> path) {
                            map.put(f.getName(), path);
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw CommonException.newInstance(e);
                }
            }
            return map;
        });
    }

    /**
     * 准测
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.2.4, 2024-04-30
     * @since 3.2.4
     */
    @Getter
    @Setter
    public static final class Criteria {

        /** 名称 */
        private String property;

        /** 操作 */
        private Operation operation;

        /** 内容 */
        private String value;
    }

    /**
     * 操作枚举
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.2.4, 2024-04-30
     * @since 3.2.4
     */
    public enum Operation {
        EQ,
        NE,
        IS_NULL,
        NOT_NULL,
        GT,
        GOE,
        LT,
        LOE,
        LIKE,
        NOT_LIKE,
        CONTAINS,
        STARTS_WITH,
        ENDS_WITH
    }
}
