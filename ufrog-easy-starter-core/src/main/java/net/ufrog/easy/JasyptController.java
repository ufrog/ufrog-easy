package net.ufrog.easy;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.ufrog.easy.authorizes.Authorize;
import net.ufrog.easy.contracts.responses.ResponseCode;
import net.ufrog.easy.contracts.responses.ResponseHeader;
import net.ufrog.easy.contracts.responses.SimpleResponse;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 参数加密控制器
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/jasypt")
@Tag(name = "参数加密服务", description = "Jasypt - 加密解密工具")
public class JasyptController {

    /** 字符串加密器 */
    private final StringEncryptor stringEncryptor;

    /** 是否启用 */
    @Value("${easy.jasypt.enabled:false}")
    private boolean enabled;

    /**
     * 参数加密
     *
     * @param value 待加密参数
     * @return 加密响应
     */
    @GetMapping("/encrypt")
    @Operation(summary = "参数加密", security = @SecurityRequirement(name = Authorize.KEY_AUTHORIZATION))
    public SimpleResponse<String> encrypt(@Parameter(description = "待加密参数", required = true, in = ParameterIn.QUERY) String value) {
        if (enabled) {
            return new SimpleResponse<>("ENC(" + stringEncryptor.encrypt(value) + ")");
        } else {
            SimpleResponse<String> resp = new SimpleResponse<>();
            resp.setHeader(new ResponseHeader(ResponseCode.FORBIDDEN));
            return resp;
        }
    }

    /**
     * 参数解密
     *
     * @param value 待解密参数
     * @return 解密响应
     */
    @GetMapping("/decrypt")
    @Operation(summary = "参数解密", security = @SecurityRequirement(name = Authorize.KEY_AUTHORIZATION))
    public SimpleResponse<String> decrypt(@Parameter(description = "待解密参数", required = true, in = ParameterIn.QUERY) String value) {
        if (enabled) {
            String val = (value.startsWith("ENC(") && value.endsWith(")")) ? value.substring("ENC(".length(), value.length() - 1) : value;
            return new SimpleResponse<>(stringEncryptor.decrypt(val));
        } else {
            SimpleResponse<String> resp = new SimpleResponse<>();
            resp.setHeader(new ResponseHeader(ResponseCode.FORBIDDEN));
            return resp;
        }
    }
}
