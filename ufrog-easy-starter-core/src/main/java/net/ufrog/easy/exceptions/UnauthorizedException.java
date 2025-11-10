package net.ufrog.easy.exceptions;

import java.io.Serial;

/**
 * 未授权异常
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
public class UnauthorizedException extends CommonException {

    @Serial
    private static final long serialVersionUID = -2154228736794730819L;

    /** 构造函数 */
    public UnauthorizedException() {
        super("User unauthorized.");
    }
}
