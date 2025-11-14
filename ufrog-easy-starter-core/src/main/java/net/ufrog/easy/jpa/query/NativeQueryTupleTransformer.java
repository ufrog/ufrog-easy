package net.ufrog.easy.jpa.query;

import net.ufrog.easy.utils.ObjectUtil;
import org.hibernate.query.TupleTransformer;

import java.io.Serializable;
import java.util.Map;

/**
 * 结构化查询元素转换器
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-13
 * @since 3.5.3
 */
public class NativeQueryTupleTransformer<T extends Serializable> implements TupleTransformer<T> {

    /** 类型 */
    private final Class<T> type;

    /**
     * 构造函数
     *
     * @param type 类型
     */
    public NativeQueryTupleTransformer(final Class<T> type) {
        this.type = type;
    }

    @Override
    public T transformTuple(Object[] tuple, String[] aliases) {
        Map<String, ObjectUtil.ClassField> mClassField = ObjectUtil.getAllClassFields(type);
        T result = ObjectUtil.newInstance(type);
        for (int i = 0; i < aliases.length; i++) {
            ObjectUtil.ClassField classField = mClassField.get(aliases[i]);
            if (classField != null) {
                classField.set(result, tuple[i]);
            }
        }
        return result;
    }
}
