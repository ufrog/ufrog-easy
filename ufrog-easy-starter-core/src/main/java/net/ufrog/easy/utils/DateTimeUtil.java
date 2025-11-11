package net.ufrog.easy.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.exceptions.CommonException;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日期时间工具
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-10
 * @since 3.5.3
 */
@Slf4j
public class DateTimeUtil {

    /** 构造函数<br>不允许外部构造 */
    private DateTimeUtil() {}

    /**
     * 创建格式化工具
     *
     * @param pattern 模式
     * @return 日期格式化
     */
    public static DateFormat newFormat(final String pattern) {
        return new SimpleDateFormat(pattern);
    }

    /**
     * 格式化日期时间
     *
     * @param date 日期时间
     * @param pattern 模式
     * @return 格式化字符串
     */
    public static String toString(final Date date, final String pattern) {
        return newFormat(pattern).format(date);
    }

    /**
     * 解析字符串为日期时间
     *
     * @param source 字符串
     * @param pattern 模式
     * @return 日期时间
     */
    public static Date fromString(final String source, final String pattern) {
        try {
            return newFormat(pattern).parse(source);
        } catch (ParseException e) {
            throw CommonException.newInstance(e);
        }
    }

    /**
     * 转换成日历
     *
     * @param date 日期时间
     * @return 日历
     */
    public static Calendar toCalendar(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 转换成开始日期时间
     *
     * @param date 日期时间
     * @param type 支持类型列表：{@link Type#YEAR}、{@link Type#QUARTER}、{@link Type#MONTH}、{@link Type#DAY}
     * @return 开始日期时间
     */
    public static Date toBeginOf(final Date date, Type type) {
        Calendar calendar = toCalendar(date);
        switch (type) {
            case YEAR:
                calendar.set(Calendar.MONTH, 0);
            case QUARTER:
                int month = calendar.get(Calendar.MONTH);
                if (month < 2) {
                    calendar.set(Calendar.MONTH, 0);
                } else if (month < 5) {
                    calendar.set(Calendar.MONTH, 3);
                } else if (month < 8) {
                    calendar.set(Calendar.MONTH, 6);
                } else if (month < 11) {
                    calendar.set(Calendar.MONTH, 9);
                }
            case MONTH:
                calendar.set(Calendar.DAY_OF_MONTH, 1);
            case DAY:
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
        }
        return calendar.getTime();
    }

    /**
     * 转换成结束日期时间
     *
     * @param date 日期时间
     * @param type 支持类型列表：{@link Type#YEAR}、{@link Type#QUARTER}、{@link Type#MONTH}、{@link Type#DAY}
     * @return 结束日期时间
     */
    public static Date toEndOf(final Date date, Type type) {
        Calendar calendar = toCalendar(toBeginOf(date, type));
        switch (type) {
            case YEAR:
                calendar.add(Calendar.YEAR, 1);
                break;
            case QUARTER:
                calendar.add(Calendar.MONTH, 3);
                break;
            case MONTH:
                calendar.add(Calendar.MONTH, 1);
                break;
            case DAY:
                calendar.add(Calendar.DATE, 1);
                break;
        }
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    /**
     * 周期转换成秒数
     *
     * @param duration 周期<br>样例：2s / 3mn / 24h / 2d
     * @return 秒数
     */
    public static int toSeconds(String duration) {
        Type[] types = new Type[] { Type.SECOND, Type.MINUTE, Type.HOUR, Type.DAY };
        for (Type type : types) {
            Matcher matcher = type.getPattern().matcher(duration);
            if (matcher.matches()) {
                return Long.valueOf(Integer.parseInt(matcher.group(1)) * type.getMilliseconds() / 1000).intValue();
            }
        }
        throw new CommonException("Invalid duration: " + duration);
    }

    /**
     * 类型枚举
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.5.3, 2025-11-10
     * @since 3.5.3
     */
    @Getter
    public enum Type {
        YEAR(0, "^([0-9]+)y$"),
        QUARTER(0, "^([0-9]+)q$"),
        MONTH(0, "^([0-9]+)m$"),
        WEEK(7 * 24 * 60 * 60 * 1000, "^([0-9]+)w$"),
        DAY(24 * 60 * 60 * 1000, "^([0-9]+)d$"),
        HOUR(60 * 60 * 1000, "^([0-9]+)h$"),
        MINUTE(60 * 1000, "^([0-9]+)mi?n$"),
        SECOND(1000, "^([0-9]+)s$");

        /** 毫秒 */
        private final long milliseconds;

        /** 表达式 */
        private final Pattern pattern;

        /**
         * 构造函数
         *
         * @param milliseconds 毫秒
         * @param regex 表达式
         */
        Type(long milliseconds, String regex) {
            this.milliseconds = milliseconds;
            this.pattern = Pattern.compile(regex);
        }
    }

    /**
     * 计时器
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.5.3, 2025-11-10
     * @since 3.5.3
     */
    public static final class Timer {

        /** 开始毫秒数 */
        private final long startTimeMillis;

        /** 构造函数 */
        public Timer() {
            startTimeMillis = System.currentTimeMillis();
        }

        /**
         * 获取花费毫秒数
         *
         * @return 花费毫秒数
         */
        public long getSpend() {
            return System.currentTimeMillis() - startTimeMillis;
        }

        /**
         * 获取花费时间
         *
         * @return 花费时间字符串
         */
        public String getSpendString() {
            long spendTimeMillis = getSpend();
            long milliseconds;
            String unit;

            if (spendTimeMillis > Type.HOUR.getMilliseconds()) {
                milliseconds = Type.HOUR.getMilliseconds();
                unit = "h";
            } else if (spendTimeMillis > Type.MINUTE.getMilliseconds()) {
                milliseconds = Type.MINUTE.getMilliseconds();
                unit = "mn";
            } else if (spendTimeMillis > Type.SECOND.getMilliseconds()) {
                milliseconds = Type.SECOND.getMilliseconds();
                unit = "s";
            } else {
                milliseconds = 1;
                unit = "ms";
            }
            return NumericUtil.divide(spendTimeMillis, milliseconds, 2, RoundingMode.HALF_UP) + unit;
        }
    }
}
