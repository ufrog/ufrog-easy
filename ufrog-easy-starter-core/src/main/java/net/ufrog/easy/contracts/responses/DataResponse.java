package net.ufrog.easy.contracts.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import net.ufrog.easy.utils.DictUtil;
import net.ufrog.easy.utils.ObjectUtil;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据响应
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(title = "数据响应")
public class DataResponse extends Response {

    @Serial
    private static final long serialVersionUID = 3199074952378078367L;

    /** 编号 */
    @Schema(title = "编号")
    private long id;

    /**
     * 设置是否为内部响应
     *
     * @param internal 是否内部响应
     * @return 数据响应
     */
    public <R extends DataResponse> R internal(boolean internal) {
        if (internal) setHeader(null);
        return ObjectUtil.cast(this);
    }

    /**
     * 基于对象创建响应
     *
     * @param bean 对象
     * @param internal 是否内部响应
     * @return 响应
     * @param <B> 对象泛型
     */
    public <R extends DataResponse, B extends Serializable> R build(final B bean, boolean internal) {
        Map<String, DictUtil.DictField> mDictField = DictUtil.getDictFields(bean.getClass());
        Map<String, ObjectUtil.ClassField> mClassField = ObjectUtil.getAllClassFields(this.getClass());
        Map<ObjectUtil.ClassField, DictUtil.DictField> mCfDf = new HashMap<>();

        // Check all dict fields and add to exclude fields
        String[] excludeFields = mDictField.entrySet().stream().filter(e -> {
            if (mClassField.containsKey(e.getKey())) {
                ObjectUtil.ClassField cf = mClassField.get(e.getKey());
                Field rf = cf.getField();
                Field bf = e.getValue().getClassField().getField();

                if (rf.getType().isAssignableFrom(DictElementResponse.class)) mCfDf.put(cf, e.getValue());
                return !rf.getDeclaringClass().equals(bf.getDeclaringClass());
            }
            return false;
        }).map(Map.Entry::getKey).toArray(String[]::new);

        // Copy bean values to response
        ObjectUtil.copy(this, bean, true, false, excludeFields);

        // Set dict element response to dict fields
        mCfDf.forEach((k, v) -> {
            Object value = v.getClassField().get(bean);
            DictUtil.Elem elem = DictUtil.get(value, v.getDictType().value());
            k.set(this, DictElementResponse.create((Serializable) value, elem, true));
        });

        // Set as internal response or not
        return internal(internal);
    }

    /**
     * 基于对象创建响应
     *
     * @param bean 对象
     * @return 响应
     * @param <B> 对象泛型
     */
    public <R extends DataResponse, B extends Serializable> R build(final B bean) {
        return build(bean, false);
    }
}
