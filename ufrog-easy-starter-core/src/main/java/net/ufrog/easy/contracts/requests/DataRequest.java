package net.ufrog.easy.contracts.requests;

import net.ufrog.easy.utils.ObjectUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.function.Consumer;

/**
 * 数据请求
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-13
 * @since 3.5.3
 */
public class DataRequest extends Request {

    @Serial
    private static final long serialVersionUID = 2485630022297704304L;

    /**
     * 转换成对象
     *
     * @param bean 对象
     * @param trim 是否修整字符串
     * @param consumer 对象消费
     * @return 返回传入的对象
     * @param <T> 对象泛型
     */
    public <T extends Serializable> T toBean(final T bean, boolean trim, Consumer<T> consumer) {
        ObjectUtil.copy(bean, this, false, trim);
        if (consumer != null) consumer.accept(bean);
        return bean;
    }

    /**
     * 转换成对象
     *
     * @param beanType 对象类型
     * @param consumer 对象消费
     * @return 新对象
     * @param <T> 对象泛型
     */
    public <T extends Serializable> T toBean(Class<T> beanType, Consumer<T> consumer) {
        T bean = ObjectUtil.newInstance(beanType);
        return toBean(bean, true, consumer);
    }

    /**
     * 转换成对象
     *
     * @param beanType 对象类型
     * @return 新对象
     * @param <T> 对象泛型
     */
    @SuppressWarnings("unused")
    public <T extends Serializable> T toBean(Class<T> beanType) {
        return toBean(beanType, null);
    }
}
