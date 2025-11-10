package net.ufrog.easy.contracts.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import net.ufrog.easy.utils.DictUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

/**
 * 字典元素响应
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(title = "字典元素响应")
public class DictElementResponse<T extends Serializable> extends DataResponse {

    @Serial
    private static final long serialVersionUID = 5098157715812165601L;

    /** 内容 */
    @Schema(title = "内容")
    private T value;

    /** 文本 */
    @Schema(title = "文本")
    private String text;

    /** 代码 */
    @Schema(title = "代码")
    private String code;

    /**
     * 创建字典元素响应
     *
     * @param value 内容
     * @param elem 字典元素
     * @param internal 是否内部响应
     * @return 字典元素响应
     * @param <T> 字典内容泛型
     */
    public static <T extends Serializable> DictElementResponse<T> create(final T value, final DictUtil.Elem elem, final boolean internal) {
        DictElementResponse<T> dictElementResponse = new DictElementResponse<>();
        dictElementResponse.setValue(value);
        Optional.ofNullable(elem).ifPresent(e -> {
            dictElementResponse.setText(e.getText());
            dictElementResponse.setCode(e.getCode());
        });
        return dictElementResponse.internal(internal);
    }

    /**
     * 创建字典元素响应
     *
     * @param value 内容
     * @param elem 字典元素
     * @return 字典元素响应
     * @param <T> 字典内容泛型
     */
    @SuppressWarnings("unused")
    public static <T extends Serializable> DictElementResponse<T> create(final T value, final DictUtil.Elem elem) {
        return create(value, elem, false);
    }
}
