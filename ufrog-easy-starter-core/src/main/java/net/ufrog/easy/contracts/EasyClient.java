package net.ufrog.easy.contracts;

import io.swagger.v3.oas.annotations.Operation;
import net.ufrog.easy.contracts.requests.DataRequest;
import net.ufrog.easy.contracts.requests.PageQueryRequest;
import net.ufrog.easy.contracts.requests.QueryRequest;
import net.ufrog.easy.contracts.responses.DataResponse;
import net.ufrog.easy.contracts.responses.ListResponse;
import net.ufrog.easy.contracts.responses.PageResponse;
import net.ufrog.easy.contracts.responses.Response;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 基础客户端
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-13
 * @since 3.5.3
 */
public interface EasyClient<RESP extends DataResponse, REQ extends DataRequest> {

    /**
     * 查询单个数据
     *
     * @param id 数据编号
     * @return 数据响应
     */
    @Operation(summary = "查询单个数据")
    @RequestMapping(value = "/find/{id}", method = RequestMethod.GET)
    RESP findOne(@PathVariable("id") long id);

    /**
     * 查询列表数据
     *
     * @param request 查询请求
     * @return 数据列表响应
     */
    @Operation(summary = "查询列表数据")
    @RequestMapping(value = "/find/list", method = RequestMethod.GET)
    ListResponse<RESP> findList(QueryRequest request);

    /**
     * 查询分页数据
     *
     * @param request 分页查询请求
     * @return 数据分页响应
     */
    @Operation(summary = "查询分页数据")
    @RequestMapping(value = "/find/page", method = RequestMethod.GET)
    PageResponse<RESP> findPage(PageQueryRequest request);

    /**
     * 创建数据
     *
     * @param request 数据请求
     * @return 数据响应
     */
    @Operation(summary = "创建数据")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    RESP create(@RequestBody REQ request);

    /**
     * 更新数据
     *
     * @param id 数据编号
     * @param request 数据请求
     * @return 数据响应
     */
    @Operation(summary = "更新数据")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    RESP update(@PathVariable("id") long id, @RequestBody REQ request);

    /**
     * 删除数据
     *
     * @param id 数据编号
     * @return 响应
     */
    @Operation(summary = "删除数据")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    Response delete(@PathVariable("id") long id);
}
