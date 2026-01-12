package net.ufrog.easy.contracts.requests;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;

/**
 * 请求
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-12
 * @since 3.5.3
 */
@Schema(title = "请求")
public class Request implements Serializable {

    @Serial
    private static final long serialVersionUID = 3949092311691152529L;
}
