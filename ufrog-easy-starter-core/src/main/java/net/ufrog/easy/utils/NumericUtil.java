package net.ufrog.easy.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.exceptions.InvalidArgumentException;
import net.ufrog.easy.i18n.I18N;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.Format;

/**
 * 数字工具
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Slf4j
public class NumericUtil {

    public static final int ONE = 1;

    /** 构造函数<br>不允许外部构造 */
    private NumericUtil() {}

    /**
     * 创建格式化工具
     *
     * @param pattern 模式
     * @return 数字格式化
     */
    public static Format newFormat(String pattern) {
        return new DecimalFormat(pattern);
    }

    /**
     * 格式化数字
     *
     * @param number 数字
     * @param pattern 模式
     * @return 格式化字符串
     */
    public static String toString(Number number, String pattern) {
        return newFormat(pattern).format(number);
    }

    /**
     * 除
     *
     * @param dividend 被除数
     * @param divisor 除数
     * @param scale 精度
     * @param roundingMode 取舍模式
     * @return 高精度数字
     */
    public static BigDecimal divide(Object dividend, Object divisor, int scale, RoundingMode roundingMode) {
        return toBigDecimal(dividend).divide(toBigDecimal(divisor), scale, roundingMode);
    }

    /**
     * 转换成小数
     *
     * @param obj 数字
     * @return 小数
     */
    public static BigDecimal toBigDecimal(Object obj) {
        if (obj == null) {
            throw new NullPointerException("obj is null.");
        } else if (obj instanceof BigDecimal value) {
            return value;
        } else if (obj instanceof Number value) {
            return BigDecimal.valueOf(value.doubleValue());
        } else if (obj instanceof String value) {
            return new BigDecimal(value);
        } else {
            throw new InvalidArgumentException(NumericUtil.class, "toBigDecimal", obj);
        }
    }

    /**
     * 货币
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.5.3, 2025-11-10
     * @since 3.5.3
     */
    @Getter
    @Setter
    public static final class Currency {

        /** 金额 */
        private BigDecimal amount;

        /** 小数位数 */
        private int scale;

        /** 取舍模式 */
        private RoundingMode roundingMode;

        /**
         * 构造函数
         *
         * @param amount 金额
         * @param scale 小数位数
         * @param roundingMode 取舍模式
         */
        public Currency(BigDecimal amount, int scale, RoundingMode roundingMode) {
            this.amount = amount;
            this.scale = scale;
            this.roundingMode = roundingMode;
        }

        /**
         * 构造函数
         *
         * @param amount 金额
         * @param scale 小数位数
         */
        public Currency(BigDecimal amount, int scale) {
            this(amount, scale, RoundingMode.HALF_UP);
        }

        /**
         * 构造函数
         *
         * @param amount 金额
         */
        public Currency(BigDecimal amount) {
            this(amount, 2);
        }

        /** 构造函数 */
        public Currency() {
            this(BigDecimal.ZERO);
        }

        /**
         * 加
         *
         * @param amount 金额
         * @return 货币
         */
        public Currency add(Object amount) {
            this.amount = this.amount.add(toBigDecimal(amount));
            return this;
        }

        /**
         * 减
         *
         * @param amount 金额
         * @return 货币
         */
        public Currency subtract(Object amount) {
            this.amount = this.amount.subtract(toBigDecimal(amount));
            return this;
        }

        /**
         * 乘
         *
         * @param amount 金额
         * @return 货币
         */
        public Currency multiply(Object amount) {
            this.amount = this.amount.multiply(toBigDecimal(amount));
            return this;
        }

        /**
         * 除
         *
         * @param amount 金额
         * @return 货币
         */
        public Currency divide(Object amount) {
            this.amount = this.amount.divide(toBigDecimal(amount), scale, roundingMode);
            return this;
        }

        @Override
        public String toString() {
            return NumericUtil.toString(amount.setScale(scale, roundingMode), I18N.get("format.currency"));
        }
    }
}
