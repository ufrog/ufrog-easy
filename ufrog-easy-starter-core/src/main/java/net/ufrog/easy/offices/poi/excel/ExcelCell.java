package net.ufrog.easy.offices.poi.excel;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;

/**
 * 表格单元格
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-16
 * @since 3.5.3
 */
@Slf4j
@Getter
@Setter
public class ExcelCell implements Serializable {

    @Serial
    private static final long serialVersionUID = 1777834446889069573L;

    public static final String ARG_DEFAULT_VALUE        = "default_value";
    public static final String ARG_FORMAT_PATTERN       = "format_pattern";
    public static final String ARG_DECIMAL_SCALE        = "decimal_scale";
    public static final String ARG_DECIMAL_ROUNDING     = "decimal_rounding";

    /** 表格单元格引用 */
    private ExcelCellRef ref;

    /** 表格单元格类型 */
    private ExcelCellType type;

    /** 表格行 */
    private ExcelRow row;

    /** 内容 */
    private Object value;

    /** 是否合并 */
    private boolean isMerged;

    /** 合并行数 */
    private int spanRows;

    /** 合并列数 */
    private int spanCols;

    /** 合并引用 */
    private ExcelCell mergeRef;
}
