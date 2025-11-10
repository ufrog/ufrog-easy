package net.ufrog.easy.exceptions;

import lombok.Getter;
import lombok.Setter;
import net.ufrog.easy.i18n.I18N;
import net.ufrog.easy.utils.StringUtil;

import java.io.Serial;

/**
 * 通用异常
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
public class CommonException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 8682569019895464296L;

    /** 随机标识 */
    @Getter
    private final String key;

    /** 消息标识 */
    @Setter
    private String messageKey;

    /** 消息参数 */
    @Setter
    private Object[] messageArguments;

    /** 构造函数 */
    @SuppressWarnings("unused")
    public CommonException() {
        super();
        key = generateKey();
        messageArguments = new Object[0];
    }

    /**
     * 构造函数
     *
     * @param message 异常消息
     */
    public CommonException(String message) {
        super(message);
        key = generateKey();
        messageArguments = new Object[0];
    }

    /**
     * 构造函数
     *
     * @param message 异常消息
     * @param cause 异常原因
     */
    public CommonException(String message, Throwable cause) {
        super(message, cause);
        key = generateKey();
        messageArguments = new Object[0];
    }

    /**
     * 构造函数
     *
     * @param message 异常消息
     * @param messageKey 消息标识
     * @param messageArguments 消息参数
     */
    public CommonException(String message, String messageKey, Object... messageArguments) {
        this(message);
        this.messageKey = messageKey;
        this.messageArguments = messageArguments;
    }

    /**
     * 构造函数
     *
     * @param message 异常消息
     * @param cause 异常原因
     * @param messageKey 消息标识
     * @param messageArguments 消息参数
     */
    @SuppressWarnings("unused")
    public CommonException(String message, Throwable cause, String messageKey, Object... messageArguments) {
        this(message, cause);
        this.messageKey = messageKey;
        this.messageArguments = messageArguments;
    }

    @Override
    public String getMessage() {
        return key + " " + super.getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        try {
            return I18N.get(messageKey, messageArguments);
        } catch (NullPointerException e) {
            return getMessage();
        }
    }

    /**
     * 生成随机标识
     *
     * @return 随机标识
     */
    private String generateKey() {
        return "@" + StringUtil.random(16);
    }

    /**
     * 创建实例
     *
     * @param e 异常
     * @return 通用异常
     */
    public static CommonException newInstance(Throwable e) {
        if (e instanceof CommonException) {
            return (CommonException) e;
        } else {
            return new CommonException(e.getMessage(), e);
        }
    }
}
