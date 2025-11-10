package net.ufrog.easy.exceptions;

import lombok.Getter;
import net.ufrog.easy.contracts.responses.ResponseCode;
import net.ufrog.easy.i18n.I18N;

import java.io.Serial;

/**
 * 响应异常
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Getter
public class ResponseException extends CommonException {

    @Serial
    private static final long serialVersionUID = 2376277065922400180L;

    /** 响应代码 */
    private final ResponseCode responseCode;

    /** 消息参数 */
    private final Object[] arguments;

    /**
     * 构造函数
     *
     * @param responseCode 响应代码
     * @param arguments 消息参数
     */
    public ResponseException(final ResponseCode responseCode, final Object... arguments) {
        this.responseCode = responseCode;
        this.arguments = arguments;
    }

    @Override
    public String getMessage() {
        return "Response code: " + responseCode.getCode() + ", " + I18N.get(responseCode.getMessageKey(), arguments);
    }
}
