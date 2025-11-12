package net.ufrog.easy.jpa;

import cn.izern.sequence.Sequence;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serial;

/**
 * 序列编号生成器
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-12
 * @since 3.5.3
 */
public class SequenceIDGenerator implements IdentifierGenerator, Configurable {

    @Serial
    private static final long serialVersionUID = 1863172598187421215L;

    private static final Sequence SEQUENCE = new Sequence();

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        ManualID manualID = object.getClass().getAnnotation(ManualID.class);
        if (manualID != null && object instanceof EasyModel model) {
            if (model.getId() != null) {
                return model.getId();
            }
        }
        return nextId();
    }

    /**
     * 生成编号
     *
     * @return 编号
     */
    public static long nextId() {
        return SEQUENCE.nextId();
    }
}
