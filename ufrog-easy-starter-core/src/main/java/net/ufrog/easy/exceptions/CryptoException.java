package net.ufrog.easy.exceptions;

import java.io.Serial;

/**
 * 加解密异常
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2026-01-22
 * @since 3.5.3
 */
public class CryptoException extends CommonException {

    @Serial
    private static final long serialVersionUID = -8471530004663357182L;

    /**
     * 构造函数
     *
     * @param message 异常消息
     */
    public CryptoException(String message) {
        super(message);
    }

    /**
     * 构造函数
     *
     * @param message 异常消息
     * @param cause 异常原因
     */
    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }
}
