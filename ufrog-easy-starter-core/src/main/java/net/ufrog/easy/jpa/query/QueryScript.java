package net.ufrog.easy.jpa.query;

import lombok.Getter;
import net.ufrog.easy.utils.MapUtil;
import net.ufrog.easy.utils.StringUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 查询脚本
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-13
 * @since 3.5.3
 */
public class QueryScript implements Serializable {

    @Serial
    private static final long serialVersionUID = 3089701007679274730L;

    private static final String FROM        = " from ";
    private static final String ORDER_BY    = " order by ";
    private static final String AND         = " and ";
    private static final String OR          = " or ";

    /** 脚本 */
    private final StringBuffer script;

    /** 参数映射 */
    @Getter
    private final Map<String, Object> parameters;

    /** 排序脚本 */
    private String orderScript;

    /** 构造函数 */
    public QueryScript() {
        this.script = new StringBuffer(256);
        this.parameters = new ConcurrentHashMap<>();
    }

    /**
     * 构造函数
     *
     * @param script 脚本
     * @param parameters 参数映射
     */
    public QueryScript(final String script, Map<String, Object> parameters) {
        this();
        parseScript(script, parameters);
    }

    /**
     * 追加脚本
     *
     * @param script 脚本
     * @param parameters 参数映射
     * @return 查询脚本
     */
    public QueryScript appendScript(final String script, final Map<String, Object> parameters) {
        StringUtil.ifNotEmpty(script, v -> this.script.append(script));
        if (parameters != null && !parameters.isEmpty()) this.parameters.putAll(parameters);
        return this;
    }

    /**
     * 追加脚本
     *
     * @param script 脚本
     * @param parameters 参数数组
     * @return 查询脚本
     */
    public QueryScript appendScript(final String script, final Object... parameters) {
        return appendScript(script, MapUtil.build(parameters));
    }

    /**
     * 设置排序脚本
     *
     * @param orderBy 排序脚本
     * @return 查询脚本
     */
    public QueryScript orderBy(final String orderBy) {
        this.orderScript = orderBy;
        return this;
    }

    /**
     * 获取没有排序脚本的查询脚本
     *
     * @return 查询脚本
     */
    public String getScriptWithoutOrderBy() {
        return script.toString();
    }

    /**
     * 读取完整查询脚本
     *
     * @return 查询脚本
     */
    public String getScript() {
        return script.toString() + (StringUtil.isEmpty(orderScript) ? "" : ORDER_BY + orderScript);
    }

    /**
     * 解析脚本
     *
     * @param script 脚本
     * @param parameters 参数映射
     */
    private void parseScript(final String script, final Map<String, Object> parameters) {
        int idx = script.lastIndexOf(ORDER_BY);
        if (idx > 0) {
            String where = script.substring(0, idx);
            String order = script.substring(idx + ORDER_BY.length());
            if (!order.contains(FROM) && !order.contains(")")) {
                appendScript(where, parameters);
                orderBy(order);
                return;
            }
        }
        appendScript(script, parameters);
        orderBy("");
    }
}
