package net.ufrog.easy.exceptions;

import net.ufrog.easy.utils.ArrayUtil;

import java.io.Serial;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 无效参数异常
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
public class InvalidArgumentException extends CommonException {

    @Serial
    private static final long serialVersionUID = -2016905543716148222L;

    /** 类型 */
    private final Class<?> clazz;

    /** 方法 */
    private final String method;

    /** 参数 */
    private final Object[] args;

    /**
     * 构造函数
     *
     * @param clazz 类型
     * @param method 方法
     * @param args 参数
     */
    public InvalidArgumentException(Class<?> clazz, String method, Object... args) {
        super(null, "common.exception.invalid-argument");
        this.clazz = clazz;
        this.method = method;
        this.args = args;
    }

    @Override
    public String getMessage() {
        return "Invalid argument for " + clazz.getSimpleName() + "." + method + toArgumentsString();
    }

    /**
     * 将参数转换成字符串
     *
     * @return 参数字符串
     */
    private String toArgumentsString() {
        if (ArrayUtil.isEmpty(args)) {
            return "";
        } else {
            return "(" + Stream.of(args).map(String::valueOf).collect(Collectors.joining(", ")) + ")";
        }
    }
}
