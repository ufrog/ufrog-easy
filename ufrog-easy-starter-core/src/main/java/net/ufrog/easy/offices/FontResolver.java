package net.ufrog.easy.offices;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 字体处理接口
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-16
 * @since 3.5.3
 */
public interface FontResolver {

    /**
     * 读取字体列表
     *
     * @return 字体列表
     */
    List<Font> getFonts();

    /**
     * 字体处理抽象
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.4.1, 2025-02-18
     * @since 3.4.1
     */
    @Getter
    class Font implements Serializable {

        @Serial
        private static final long serialVersionUID = 1919638186551807782L;

        /**
         * 构造函数
         *
         * @param path 路径
         * @param name 名称
         */
        public Font(String path, String name) {
            this.path = path;
            this.name = name;
        }

        /** 路径 */
        private final String path;

        /** 名称 */
        private final String name;
    }
}
