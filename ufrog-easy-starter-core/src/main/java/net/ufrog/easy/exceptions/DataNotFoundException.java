package net.ufrog.easy.exceptions;

import net.ufrog.easy.utils.MapUtil;

import java.io.Serial;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 无数据异常
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
public class DataNotFoundException extends CommonException {

    @Serial
    private static final long serialVersionUID = 5038550456067115132L;

    /** 类型 */
    private final Class<?> type;

    /** 参数 */
    private final Map<String, Object> arguments;

    /**
     * 构造函数
     *
     * @param type 类型
     * @param arguments 参数
     */
    public DataNotFoundException(Class<?> type, Object... arguments) {
        super(null, "common.exception.data-not-found");
        this.type = type;
        this.arguments = MapUtil.build(arguments);
    }

    @Override
    public String getMessage() {
        return getKey() + " Cannot find data '" +
                type.getName() + "' with arguments (" +
                arguments.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(",")) +
                ").";
    }
}
