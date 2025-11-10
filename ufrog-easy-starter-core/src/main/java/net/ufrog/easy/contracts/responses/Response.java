package net.ufrog.easy.contracts.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import net.ufrog.easy.utils.ObjectUtil;

import java.io.Serial;
import java.io.Serializable;

/**
 * 响应
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(title = "响应")
public class Response implements Serializable {

    @Serial
    private static final long serialVersionUID = -1243422111895875766L;

    /** 响应头 */
    @Schema(title = "响应头")
    private ResponseHeader header;

    /** 构造函数 */
    public Response() {
        this.header = new ResponseHeader();
    }

    /**
     * 新建实例
     *
     * @param code 响应代码
     * @param message 消息
     * @param type 响应类型
     * @return 响应
     * @param <T> 响应泛型
     */
    public static <T extends Response> T newInstance(ResponseCode code, String message, Class<T> type) {
        T resp = ObjectUtil.newInstance(type);
        resp.setHeader(new ResponseHeader(code, message));
        return resp;
    }

    /**
     * 新建实例
     *
     * @param code 响应代码
     * @param type 响应类型
     * @return 响应
     * @param <T> 响应泛型
     */
    public static <T extends Response> T newInstance(ResponseCode code, Class<T> type) {
        return newInstance(code, null, type);
    }

    /**
     * 新建实例
     *
     * @param code 响应代码
     * @return 响应
     */
    public static Response newInstance(ResponseCode code) {
        return newInstance(code, null, Response.class);
    }
}
