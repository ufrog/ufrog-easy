package net.ufrog.easy.offices;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-30
 * @since 3.5.3
 */
@Configuration
public class ConfigBean {

    @Bean
    public FontResolver fontResolver() {
        return () -> {
            List<FontResolver.Font> fonts = new ArrayList<>();
            fonts.add(new FontResolver.Font("msyh.ttc", "Microsoft YaHei UI"));
            return fonts;
        };
    }
}
