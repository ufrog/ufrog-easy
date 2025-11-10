package net.ufrog.easy.contracts.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import net.ufrog.easy.utils.StringUtil;

import java.io.Serial;
import java.io.Serializable;

/**
 * 响应代码
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(title = "响应代码")
public class ResponseCode implements Serializable {

    @Serial
    private static final long serialVersionUID = 3995196855340592292L;

    public static final ResponseCode OK                         = new ResponseCode("A000200", 200, true);
    public static final ResponseCode ACCEPTED                   = new ResponseCode("A000202", 202, true);
    public static final ResponseCode UNAUTHORIZED               = new ResponseCode("A000401", 401, false);
    public static final ResponseCode FORBIDDEN                  = new ResponseCode("A000403", 403, false);
    public static final ResponseCode NOT_FOUND                  = new ResponseCode("A000404", 404, false);
    public static final ResponseCode INTERNAL_SERVER_ERROR      = new ResponseCode("A000500");

    /** 代码 */
    @Schema(title = "代码")
    private final String code;

    /** 消息标识 */
    @JsonIgnore
    private final String messageKey;

    /** 状态代码 */
    @JsonIgnore
    private final int statusCode;

    /** 是否成功 */
    @Schema(title = "是否成功")
    private final boolean isSuccess;

    /**
     * 构造函数
     *
     * @param code 代码
     * @param messageKey 消息标识
     * @param statusCode 状态代码
     * @param isSuccess 是否成功
     */
    public ResponseCode(String code, String messageKey, int statusCode, boolean isSuccess) {
        this.code = code;
        this.messageKey = messageKey;
        this.statusCode = statusCode;
        this.isSuccess = isSuccess;
    }

    /**
     * 构造函数
     *
     * @param code 代码
     * @param statusCode 状态代码
     * @param isSuccess 是否成功
     */
    public ResponseCode(String code, int statusCode, boolean isSuccess) {
        this(code, null, statusCode, isSuccess);
    }

    /**
     * 构造函数
     *
     * @param code 代码
     * @param messageKey 消息标识
     */
    public ResponseCode(String code, String messageKey) {
        this(code, messageKey, 500, false);
    }

    /**
     * 构造函数
     *
     * @param code 代码
     */
    public ResponseCode(String code) {
        this(code, null);
    }

    /**
     * 读取消息标识
     *
     * @return 消息标识
     */
    public String getMessageKey() {
        if (StringUtil.isEmpty(messageKey)) {
            return "response." + code;
        }
        return messageKey;
    }
}
