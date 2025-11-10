package net.ufrog.easy.contracts.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import net.ufrog.easy.i18n.I18N;
import net.ufrog.easy.utils.StringUtil;

import java.io.Serial;
import java.io.Serializable;

/**
 * 响应头
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(title = "响应头")
public class ResponseHeader implements Serializable {

    @Serial
    private static final long serialVersionUID = -2973947382305120169L;

    /** 代码 */
    @Getter
    @Schema(title = "响应代码")
    private ResponseCode code;

    /** 消息 */
    @Schema(title = "响应消息")
    private String message;

    /**
     * 构造函数
     *
     * @param code 代码
     * @param message 消息
     */
    public ResponseHeader(ResponseCode code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数
     *
     * @param code 代码
     */
    public ResponseHeader(ResponseCode code) {
        this(code, null);
    }

    /** 构造函数 */
    public ResponseHeader() {
        this(ResponseCode.OK);
    }

    /**
     * 读取消息
     *
     * @return 消息
     */
    public String getMessage() {
        return StringUtil.getOrElse(message, () -> {
            String messageKey = StringUtil.getOrDefault(code.getMessageKey(), "response." + code.getCode());
            return I18N.get(messageKey);
        });
    }
}
