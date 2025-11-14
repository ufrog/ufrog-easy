package net.ufrog.easy.contracts.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.io.Serial;
import java.util.Collection;

/**
 * 分页响应
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-13
 * @since 3.5.3
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(title = "分页响应")
public class PageResponse<T extends Response> extends ListResponse<T> {

    @Serial
    private static final long serialVersionUID = 161468146437608990L;

    /** 分页大小 */
    @Schema(title = "分页大小")
    private int size;

    /** 当前页号 */
    @Schema(title = "当前页号")
    private int number;

    /** 记录总数 */
    @Schema(title = "总记录数")
    private long totalElements;

    /** 构造函数 */
    public PageResponse() {
        super();
    }

    /**
     * 构造函数
     *
     * @param size 分页大小
     * @param number 当前页号
     * @param totalElements 构造函数
     * @param content 数据集合
     */
    public PageResponse(int size, int number, long totalElements, Collection<T> content) {
        this();
        this.size = size;
        this.number = number;
        this.totalElements = totalElements;
        this.getContent().addAll(content);
    }

    /**
     * 构造函数
     *
     * @param page 分页信息
     * @param content 数据集合
     */
    @SuppressWarnings("unused")
    public PageResponse(Page<?> page, Collection<T> content) {
        this(page.getSize(), page.getNumber(), page.getTotalElements(), content);
    }

    /**
     * 读取总页数
     *
     * @return 总页数
     */
    @Schema(title = "总页数")
    public int getTotalPages() {
        if (size > 0 && totalElements >= 0) {
            return (int) Math.ceil((double) totalElements / (double) size);
        }
        return 0;
    }

    /**
     * 读取当前页记录数
     *
     * @return 当前页记录数
     */
    @SuppressWarnings("unused")
    @Schema(title = "当前页记录数")
    public int getNumberOfElements() {
        return getContent().size();
    }

    /**
     * 是否首页
     *
     * @return 判断结果
     */
    @SuppressWarnings("unused")
    @Schema(title = "是否第一页")
    public boolean isFirst() {
        return number == 0;
    }

    /**
     * 是否尾页
     *
     * @return 判断结果
     */
    @SuppressWarnings("unused")
    @Schema(title = "是否最后页")
    public boolean isLast() {
        return number == (getTotalPages() - 1);
    }
}
