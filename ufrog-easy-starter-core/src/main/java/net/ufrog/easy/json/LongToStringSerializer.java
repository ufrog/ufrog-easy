package net.ufrog.easy.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.IOException;
import java.io.Serial;

/**
 * 长整型转换成字符串序列器
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
public class LongToStringSerializer extends ToStringSerializer {

    @Serial
    private static final long serialVersionUID = -2916693391422019145L;

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value instanceof Long val) {
            if (val > 9007199254740991L) {
                gen.writeString(String.valueOf(val));
            } else {
                gen.writeNumber(val);
            }
        } else {
            super.serialize(value, gen, provider);
        }
    }
}
