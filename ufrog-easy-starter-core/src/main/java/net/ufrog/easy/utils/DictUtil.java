package net.ufrog.easy.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.caches.CacheUtil;
import net.ufrog.easy.i18n.I18N;

import java.io.Serial;
import java.io.Serializable;
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 字典工具
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Slf4j
public class DictUtil {

    private static final String PREFIX = "dict_";
    private static final Map<Class<?>, Map<String, DictField>> DICT_FIELD_MAP = new ConcurrentHashMap<>();

    /** 构造函数<br>不允许外部构造 */
    private DictUtil() {}

    /**
     * 读取字典元素映射
     *
     * @param type 字典类型
     * @return 字典元素映射
     */
    public static Map<Object, Elem> getElements(Class<?> type) {
        return CacheUtil.computeMapIfAbsent(PREFIX + type.getName(), Object.class, Elem.class, () -> {
            log.info("Start caching dict for type {}...", type.getName());
            Map<Object, Elem> map = new LinkedHashMap<>();

            // Loop all fields to find @Element field
            for (Field field: type.getDeclaredFields()) {
                Optional.ofNullable(field.getAnnotation(Element.class)).ifPresent(e -> {
                    try {
                        Object val = field.get(type);
                        Elem elem = new Elem(val, e);
                        map.put(val, elem);
                        log.info("Finding dict for field {} - {}.", elem.getValue(), elem.getText());
                    } catch (IllegalAccessException ex) {
                        log.error(ex.getMessage(), ex);
                    }
                });
            }
            log.info("End caching dict for type {}.", type.getName());
            return new CacheUtil.SupplierWrapper<>(map);
        });
    }

    /**
     * 读取字典元素
     *
     * @param value 元素内容
     * @param type 字典类型
     * @return 字典元素
     */
    public static Elem get(Object value, Class<?> type) {
        return getElements(type).get(value);
    }

    /**
     * 通过文本读取字典元素
     *
     * @param text 元素文本
     * @param type 字典类型
     * @return 字典元素
     */
    public static Elem getFromText(String text, Class<?> type) {
        return getElements(type).values().stream().filter(elem -> StringUtil.equals(elem.getText(), text)).findFirst().orElse(null);
    }

    /**
     * 读取元素文本
     *
     * @param value 元素内容
     * @param type 字典类型
     * @return 字典元素
     */
    public static String getText(Object value, Class<?> type) {
        return Optional.ofNullable(get(value, type)).map(Elem::getText).orElse(null);
    }

    /**
     * 读取元素代码
     *
     * @param value 元素内容
     * @param type 字典类型
     * @return 字典元素
     */
    public static String getCode(Object value, Class<?> type) {
        return Optional.ofNullable(get(value, type)).map(Elem::getCode).orElse(null);
    }

    /**
     * 读取字典类型字段映射
     *
     * @param type 类型
     * @return 字典字段映射
     */
    public static Map<String, DictField> getDictFields(Class<?> type) {
        return DICT_FIELD_MAP.computeIfAbsent(type, t -> {
            Map<String, DictField> map = new HashMap<>();
            ObjectUtil.getAllClassFields(t).forEach((k, v) -> Optional.ofNullable(v.getField().getAnnotation(DictType.class)).ifPresent(dt -> map.put(k, new DictField(dt, v))));
            return map;
        });
    }

    /**
     * 字典元素注解
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.5.3, 2025-11-10
     * @since 3.5.3
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface Element {

        /** 元素文本 */
        String text();

        /** 元素代码 */
        String code() default "";
    }

    /**
     * 字典元素
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.5.3, 2025-11-10
     * @since 3.5.3
     */
    public static final class Elem implements Serializable {

        @Serial
        private static final long serialVersionUID = -189387741745724443L;

        /** 内容 */
        @Getter
        private final Object value;

        /** 文本 */
        private final String text;

        /** 代码 */
        @Getter
        private final String code;

        /**
         * 构造函数
         *
         * @param value 内容
         * @param text 文本
         * @param code 代码
         */
        public Elem(Object value, String text, String code) {
            this.value = value;
            this.text = text;
            this.code = code;
        }

        /**
         * 构造函数
         *
         * @param value 内容
         * @param element 字典元素注解
         */
        public Elem(Object value, Element element) {
            this(value, element.text(), element.code());
        }

        /**
         * 构造函数
         *
         * @param value 内容
         * @param text 文本
         */
        public Elem(Object value, String text) {
            this(value, text, null);
        }

        /**
         * 读取文本
         *
         * @return 文本
         */
        public String getText() {
            if (text.startsWith("@")) {
                return I18N.get(text.substring(1));
            } else {
                return text;
            }
        }
    }

    /**
     * 电子类型标注
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.5.3, 2025-11-10
     * @since 3.5.3
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface DictType {

        /** 类型 */
        Class<?> value();
    }

    /**
     * 字典字段
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.5.3, 2025-11-10
     * @since 3.5.3
     */
    @SuppressWarnings("ClassCanBeRecord")
    @Getter
    public static final class DictField implements Serializable {

        @Serial
        private static final long serialVersionUID = 1165596104573834136L;

        /** 字典类型注解 */
        private final DictType dictType;

        /** 对象字段 */
        private final ObjectUtil.ClassField classField;

        /**
         * 构造函数
         *
         * @param dictType 字典类型注解
         * @param classField 对象字段
         */
        public DictField(DictType dictType, ObjectUtil.ClassField classField) {
            this.dictType = dictType;
            this.classField = classField;
        }
    }

    /**
     * 布尔
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.5.3, 2025-11-10
     * @since 3.5.3
     */
    public static final class Bool {

        @Element(text = "@dict.bool.false")
        public static final String FALSE    = "00";

        @Element(text = "@dict.bool.true")
        public static final String TRUE     = "10";
    }
}
