package net.ufrog.easy.jpa;

import java.lang.annotation.*;

/**
 * 允许手动生成编号标注
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-12
 * @since 3.5.3
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ManualID {
}
