package net.ufrog.easy.authorizes;

import java.lang.annotation.*;

/**
 * 认证忽略
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthorizeIgnore {
}
