package net.ufrog.easy;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.authorizes.AuthorizeIgnore;
import net.ufrog.easy.contracts.responses.Response;
import net.ufrog.easy.contracts.responses.SimpleResponse;
import net.ufrog.easy.i18n.I18N;
import net.ufrog.easy.i18n.I18NChangeCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 国际化控制器
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-12
 * @since 3.5.3
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/i18n")
@Tag(name = "国际化服务")
public class I18NController {

    /** 国际化语言切换回调接口 */
    private final I18NChangeCallback i18NChangeCallback;

    /**
     * 切换语言
     *
     * @param localeStr 地区字符串
     * @return 响应
     */
    @GetMapping("/locale/{localeStr}")
    @Operation(summary = "切换语言")
    @AuthorizeIgnore
    public Response change(@PathVariable("localeStr") String localeStr) {
        i18NChangeCallback.beforeChange(localeStr);
        I18N.setSessionLocale(localeStr);
        i18NChangeCallback.afterChange(localeStr);
        return new Response();
    }

    /**
     * 读取所有国际化消息
     *
     * @return 国际化消息简单响应
     */
    @GetMapping("/messages")
    @Operation(summary = "读取所有国际化消息")
    @AuthorizeIgnore
    public SimpleResponse<HashMap<String, String>> getMessages() {
        Map<String, String> defaultMessages = I18N.getMessageSource().getAll(null);
        Map<String, String> localeMessages = I18N.getMessageSource().getAll(I18N.getSessionLocale());

        defaultMessages.putAll(localeMessages);
        if (log.isDebugEnabled()) {
            log.debug(">>>>>>>>>> {}", I18N.getSessionLocale());
            for (Map.Entry<String, String> entry : defaultMessages.entrySet()) {
                log.debug(">>>>>>>>>>>> {}: {}", entry.getKey(), entry.getValue());
            }
        }
        return new SimpleResponse<>(new HashMap<>(defaultMessages));
    }
}
