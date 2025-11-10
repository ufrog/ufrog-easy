package net.ufrog.easy.contracts.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 简单响应
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(title = "简单响应")
public class SimpleResponse<T extends Serializable> extends DataResponse {

    @Serial
    private static final long serialVersionUID = 7324842037365782713L;

    /** 数据 */
    private T data;

    /** 构造函数 */
    public SimpleResponse() {
        super();
    }

    /**
     * 构造函数
     *
     * @param data 数据
     */
    public SimpleResponse(T data) {
        this();
        this.data = data;
    }
}
