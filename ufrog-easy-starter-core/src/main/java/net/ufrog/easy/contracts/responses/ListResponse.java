package net.ufrog.easy.contracts.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 列表响应
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-13
 * @since 3.5.3
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(title = "列表响应")
public class ListResponse<T extends Response> extends Response {

    @Serial
    private static final long serialVersionUID = 2085543597933205843L;

    /** 数据集合 */
    @Schema(title = "数据集合")
    private final Collection<T> content;

    /** 构造函数 */
    public ListResponse() {
        this.content = new ArrayList<>();
    }

    /**
     * 构造函数
     *
     * @param content 数据集合
     */
    public ListResponse(Collection<T> content) {
        this();
        this.content.addAll(content);
    }

    /**
     * 判断列表是否为空
     *
     * @return 判断结果
     */
    @Schema(title = "是否无数据")
    public boolean isEmpty() {
        return content.isEmpty();
    }
}
