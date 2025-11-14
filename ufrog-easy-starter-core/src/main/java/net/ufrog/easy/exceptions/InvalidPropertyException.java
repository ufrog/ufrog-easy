package net.ufrog.easy.exceptions;

import java.io.Serial;

/**
 * 无效属性异常
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-12
 * @since 3.5.3
 */
public class InvalidPropertyException extends CommonException {

    @Serial
    private static final long serialVersionUID = -4249316431072994844L;

    /** 前缀 */
    private final String prefix;

    /** 名称 */
    private final String name;

    /** 内容 */
    private final String value;

    /**
     * 构造函数
     *
     * @param prefix 前缀
     * @param name 名称
     * @param value 内容
     */
    public InvalidPropertyException(String prefix, String name, String value) {
        super(null, "common.exception.invalid-property");
        this.prefix = prefix;
        this.name = name;
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Invalid property '" + prefix + "." + name + " = " + value;
    }
}
