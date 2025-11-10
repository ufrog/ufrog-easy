package net.ufrog.easy.utils;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.exceptions.CommonException;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * 对象工具
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Slf4j
public class ObjectUtil {

    private static final Map<Class<?>, Map<String, ClassField>> CLASS_FIELD_MAP = new HashMap<>();
    private static final Map<Class<?>, Class<?>> BASE_CLASS_MAP                 = new HashMap<>();

    static {
        BASE_CLASS_MAP.put(Byte.class, Byte.class);
        BASE_CLASS_MAP.put(byte.class, Byte.class);
        BASE_CLASS_MAP.put(Short.class, Short.class);
        BASE_CLASS_MAP.put(short.class, Short.class);
        BASE_CLASS_MAP.put(Integer.class, Integer.class);
        BASE_CLASS_MAP.put(int.class, Integer.class);
        BASE_CLASS_MAP.put(Long.class, Long.class);
        BASE_CLASS_MAP.put(long.class, Long.class);
        BASE_CLASS_MAP.put(Float.class, Float.class);
        BASE_CLASS_MAP.put(float.class, Float.class);
        BASE_CLASS_MAP.put(Double.class, Double.class);
        BASE_CLASS_MAP.put(double.class, Double.class);
        BASE_CLASS_MAP.put(Character.class, Character.class);
        BASE_CLASS_MAP.put(char.class, Character.class);
        BASE_CLASS_MAP.put(Boolean.class, Boolean.class);
        BASE_CLASS_MAP.put(boolean.class, Boolean.class);
    }

    /** 构造函数<br>不允许外部构造 */
    private ObjectUtil() {}

    /**
     * 强制转换
     *
     * @param obj 对象
     * @return 类型对象
     * @param <T> 类型泛型
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    /**
     * 若对象存在则进行处理
     *
     * @param value 对象
     * @param consumer 消费方法
     * @param <T> 对象泛型
     */
    public static <T> void ifPresent(T value, Consumer<T> consumer) {
        if (value != null && consumer != null) {
            consumer.accept(value);
        }
    }

    /**
     * 判断对象是否可序列化
     *
     * @param obj 对象
     * @return 判断结果
     */
    public static boolean isSerializable(Object obj) {
        return obj == null || obj instanceof Serializable;
    }

    /**
     * 复制对象
     *
     * @param dest 目标对象
     * @param source 原始对象
     * @param nullable 是否复制空值
     * @param trim 是否修整字符串
     * @param excludeFields 排除字段
     * @return 目标对象
     * @param <T> 目标对象泛型
     */
    public static <T> T copy(final T dest, final Object source, boolean nullable, boolean trim, String... excludeFields) {
        Map<String, ClassField> mDestClassField = getAllClassFields(dest.getClass());
        Map<String, ClassField> mSourceClassField = getAllClassFields(source.getClass());

        // Loop dest class fields
        mDestClassField.forEach((k, v) -> {
            if (ArrayUtil.isEmpty(excludeFields) || !StringUtil.in(k, excludeFields)) {
                if (mSourceClassField.containsKey(k)) {
                    ClassField classField = mSourceClassField.get(k);
                    Object value = classField.get(source);
                    Class<?>[] params = v.getSetter().getParameterTypes();

                    // Check params length and null
                    if (params.length != 1 || !equalsClass(classField.getGetter().getReturnType(), params[0])) {
                        log.trace("Cannot copy field {}.{}: {} to field {}.{}: {}.", classField.getClazz(), k, classField.getGetter().getReturnType(), v.getClazz(), k, params[0]);
                    } else if (value == null && !nullable) {
                        log.trace("Source field {} value is null, and nullable is false.", k);
                    } else {
                        if (value instanceof String str && trim) value = str.trim();
                        v.set(dest, value);
                    }
                } else {
                    log.trace("Cannot find field {} from source type {}.", k, source.getClass());
                }
            } else {
                log.debug("Field '{}' is excluded.", k);
            }
        });
        return dest;
    }

    /**
     * 克隆对象
     *
     * @param source 原始对象
     * @param excludeFields 排除字段
     * @return 克隆对象
     * @param <T> 对象泛型
     */
    public static <T> T clone(final T source, String... excludeFields) {
        T dest = cast(newInstance(source.getClass()));
        return copy(dest, source, false, false, excludeFields);
    }

    /**
     * 读取类型<br>
     * 基础类型转换
     *
     * @param clazz 类型
     * @return 类型
     */
    public static Class<?> getType(final Class<?> clazz) {
        return BASE_CLASS_MAP.getOrDefault(clazz, clazz);
    }

    /**
     * 判断类型是否相同
     *
     * @param one 待判断类型
     * @param another 待判断类型
     * @return 判断结果
     */
    public static boolean equalsClass(final Class<?> one, final Class<?> another) {
        Class<?> cls1 = getType(one);
        Class<?> cls2 = getType(another);
        return cls1 == cls2;
    }

    /**
     * 新建对象实例
     *
     * @param clazz 对象类型
     * @param args 构造函数参数
     * @return 新建对象
     * @param <T> 对象泛型
     */
    public static <T> T newInstance(final Class<T> clazz, final Object... args) {
        try {
            if (clazz == null) throw new NullPointerException("Class type is null.");
            Class<?>[] types = ArrayUtil.isEmpty(args) ? new Class<?>[0] : Stream.of(args).map(Object::getClass).toArray(Class[]::new);
            Constructor<T> constructor = clazz.getConstructor(types);
            return constructor.newInstance(args);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 新建对象实例
     *
     * @param className 类名
     * @param clazz 对象类型
     * @param args 构造函数参数
     * @return 新建对象
     * @param <T> 对象泛型
     */
    public static <T> T newInstance(final String className, final Class<T> clazz, final Object... args) {
        try {
            return clazz.cast(newInstance(Class.forName(className), args));
        } catch (ClassNotFoundException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 读取所有声明字段
     *
     * @param clazz 类型
     * @return 声明字段列表
     */
    public static List<Field> getAllDeclaredFields(final Class<?> clazz) {
        List<Field> list = new ArrayList<>();
        Class<?> superClass = clazz.getSuperclass();
        Field[] fields = clazz.getDeclaredFields();

        if (superClass != null) {
            list.addAll(getAllDeclaredFields(superClass));
        } if (!ArrayUtil.isEmpty(fields)) {
            list.addAll(Arrays.asList(fields));
        }
        return list;
    }

    /**
     * 读取所有类型字段
     *
     * @param clazz 类型
     * @return 类型字段映射
     */
    public static Map<String, ClassField> getAllClassFields(final Class<?> clazz) {
        return Optional.ofNullable(CLASS_FIELD_MAP.get(clazz)).orElseGet(() -> {
            Map<String, ClassField> map = new HashMap<>();
            getAllDeclaredFields(clazz).stream().filter(field -> !Modifier.isFinal(field.getModifiers())).forEach(field -> {
                try {
                    ClassField classField = new ClassField(field);
                    map.put(field.getName(), classField);
                    log.debug("Find class field: {}.", field.getName());
                } catch (Exception e) {
                    throw CommonException.newInstance(e);
                }
            });
            CLASS_FIELD_MAP.put(clazz, map);
            log.info("Get class fields from {}.", clazz.getName());
            return map;
        });
    }

    /**
     * 读取泛型
     *
     * @param clazz 类
     * @return 泛型数组
     */
    public static Class<?>[] getGenericTypes(final Class<?> clazz) {
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            Class<?>[] classes = new Class[parameterizedType.getActualTypeArguments().length];
            for (int i = 0; i < parameterizedType.getActualTypeArguments().length; i++) {
                classes[i] = (Class<?>) parameterizedType.getActualTypeArguments()[i];
            }
            return classes;
        }
        return new Class<?>[0];
    }

    /**
     * 读取包下所有类型
     *
     * @param packageName 包名
     * @param recursive 是否迭代
     * @return 类型集合
     */
    public static Set<Class<?>> getAllClasses(final String packageName, final boolean recursive) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        String packageDirName = packageName.replace('.', '/');

        try {
            Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();

                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8);
                    classes.addAll(getAllClasses(packageName, filePath, recursive));
                } else if ("jar".equals(protocol)) {
                    JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String entryName = entry.getName();
                        if (entryName.charAt(0) == '/') entryName = entryName.substring(1);
                        if (entryName.startsWith(packageDirName)) {
                            int idx = entryName.lastIndexOf('/');
                            String jarPackageName = packageName;
                            if (idx != -1) jarPackageName = entryName.substring(0, idx).replace('/', '.');
                            if (idx != -1 || recursive) {
                                if (entryName.endsWith(".class") && !entry.isDirectory()) {
                                    String className = jarPackageName + "." + entryName.substring(jarPackageName.length() + 1, entryName.length() - 6);
                                    Optional.ofNullable(loadClass(className)).ifPresent(classes::add);
                                }
                            }
                        }
                    }
                }
            }
            return classes;
        } catch (IOException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 读取包下所有类型
     *
     * @param packageName 包名
     * @param packagePath 包路径
     * @param recursive 是否迭代
     * @return 类型集合
     */
    public static Set<Class<?>> getAllClasses(final String packageName, final String packagePath, final boolean recursive) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        File dir = new File(packagePath);

        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles(file -> (file.isDirectory() && recursive) || file.getName().endsWith(".class"));
            if (ArrayUtil.isEmpty(files)) return classes;
            for (File file : files) {
                if (file.isDirectory()) {
                    classes.addAll(getAllClasses(packageName + "." + file.getName(), file.getAbsolutePath(), recursive));
                } else {
                    String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                    Optional.ofNullable(loadClass(className)).ifPresent(classes::add);
                }
            }
        }
        return classes;
    }

    /**
     * 加载类型
     *
     * @param className 类名
     * @return 类型
     */
    public static Class<?> loadClass(final String className) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            log.warn("Cannot find class {}: {}.", className, e.getMessage());
            return null;
        }
    }

    /**
     * 获取读取方法
     *
     * @param clazz 类型
     * @param field 字段名称
     * @return 读取方法
     */
    public static Method getter(Class<?> clazz, String field) {
        String method = field.substring(0, 1).toUpperCase() + field.substring(1);
        try {
            return clazz.getDeclaredMethod("get" + method);
        } catch (NoSuchMethodException e) {
            try {
                log.debug("Cannot find getter method get{} from type {}, try to find is{}.", method, clazz.getName(), method);
                Method getter = clazz.getDeclaredMethod("is" + method);
                if (getter.getReturnType() != Boolean.TYPE) throw new NoSuchMethodException("Method is" + method + " return type is not boolean.");
                return getter;
            } catch (NoSuchMethodException ex) {
                log.warn("There isn't getter method for field {} from type {}.", field, clazz.getName());
                return null;
            }
        }
    }

    /**
     * 获取读取方法
     *
     * @param field 字段
     * @return 读取方法
     */
    public static Method getter(Field field) {
        return getter(field.getDeclaringClass(), field.getName());
    }

    /**
     * 获取设置方法
     *
     * @param clazz 类型
     * @param field 字段名称
     * @param fieldType 字段类型
     * @return 设置方法
     */
    public static Method setter(Class<?> clazz, String field, Class<?> fieldType) {
        try {
            return clazz.getDeclaredMethod("set" + field.substring(0, 1).toUpperCase() + field.substring(1), fieldType);
        } catch (NoSuchMethodException e) {
            log.warn("There isn't setter method for field {} from type {}.", field, clazz.getName());
            return null;
        }
    }

    /**
     * 获取设置方法
     *
     * @param field 字段
     * @return 设置方法
     */
    public static Method setter(Field field) {
        return setter(field.getDeclaringClass(), field.getName(), field.getType());
    }

    /**
     * 开箱
     *
     * @param value 待开箱值
     * @return 开箱后的值
     */
    public static long unbox(@Nullable Long value) {
        if (value == null) throw new CommonException("Cannot unbox null value");
        return value;
    }

    /**
     * 真假处理
     *
     * @param condition 条件
     * @param trueAction 条件真处理
     * @param falseAction 条件假处理
     */
    public static void ifTrueOrElse(boolean condition, Runnable trueAction, Runnable falseAction) {
        if (condition && trueAction != null) {
            trueAction.run();
        } else if (!condition && falseAction != null) {
            falseAction.run();
        }
    }

    /**
     * 对象字段
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.5.3, 2025-11-10
     * @since 3.5.3
     */
    @Getter
    public static final class ClassField {

        /** 类型 */
        private final Class<?> clazz;

        /** 字段 */
        private final Field field;

        /** 设置方法 */
        private final Method getter;

        /** 读取方法 */
        private final Method setter;

        /**
         * 构造函数
         *
         * @param field 字段
         */
        public ClassField(Field field) {
            this.field = field;
            this.clazz = field.getDeclaringClass();
            this.getter = getter(field);
            this.setter = setter(field);
        }

        /**
         * 获取内容
         *
         * @param obj 对象
         * @return 对象字段内容
         */
        public Object get(Object obj) {
            try {
                if (!isGettable()) throw new NoSuchMethodException("There isn't getter method for field " + field.getName() + " from type " + clazz.getName());
                return getter.invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw CommonException.newInstance(e);
            }
        }

        /**
         * 是否可以获取内容
         *
         * @return 判断结果
         */
        public boolean isGettable() {
            return getter != null;
        }

        /**
         * 设置内容
         *
         * @param obj 对象
         * @param value 对象字段内容
         */
        public void set(Object obj, Object value) {
            try {
                if (!isSettable()) throw new NoSuchMethodException("There isn't setter method for field " + field.getName() + " from type " + clazz.getName());
                setter.invoke(obj, value);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw CommonException.newInstance(e);
            }
        }

        /**
         * 是否可以设置内容
         *
         * @return 判断结果
         */
        public boolean isSettable() {
            return setter != null;
        }
    }
}
