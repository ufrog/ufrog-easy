package net.ufrog.easy.contracts.requests;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.Setter;
import net.ufrog.easy.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serial;

/**
 * 分页查询请求
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-12
 * @since 3.5.3
 */
@Setter
public class PageQueryRequest extends QueryRequest {

    @Serial
    private static final long serialVersionUID = 7262606896896044460L;

    /** 分页大小 */
    @Parameter(name = "_size", in = ParameterIn.QUERY, description = "分页大小", example = "20")
    private int size;

    /** 目标页号 */
    @Parameter(name = "_page", in = ParameterIn.QUERY, description = "目标页号，从0开始", example = "0")
    private int page;

    /** 构造函数 */
    public PageQueryRequest() {
        super();
        this.size = -1;
        this.page = -1;
    }

    /**
     * 读取分页信息
     *
     * @return 分页信息
     */
    public Pageable getPageable() {
        return getPageable(getSort());
    }

    /**
     * 读取分页信息
     *
     * @param sort 排序
     * @return 分页信息
     */
    public Pageable getPageable(Sort sort) {
        return PageRequest.of(getPage(), getSize(), sort);
    }

    /**
     * 读取分页信息
     *
     * @param direction 排序方向
     * @param properties 排序字段
     * @return 分页信息
     */
    @SuppressWarnings("unused")
    public Pageable getPageable(Sort.Direction direction, String... properties) {
        return getPageable(Sort.by(direction, properties));
    }

    /**
     * 读取分页大小
     *
     * @return 分页大小
     */
    public int getSize() {
        return size <= 0 ? ApplicationContext.getIntProperty("page_size", 20) : size;
    }

    /**
     * 读取目标页号
     *
     * @return 目标页号
     */
    public int getPage() {
        return Math.max(page, 0);
    }
}
