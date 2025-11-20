package net.ufrog.easy.offices.poi.excel;

import lombok.Getter;
import net.ufrog.easy.utils.StringUtil;

import java.io.Serial;
import java.io.Serializable;

/**
 * 表格单元格引用
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-16
 * @since 3.5.3
 */
@Getter
public class ExcelCellRef implements Serializable {

    @Serial
    private static final long serialVersionUID = -2407687070239272872L;

    /** 引用 */
    private final String ref;

    /** 行代码 */
    private final String rowCode;

    /** 行号 */
    private final int rowNum;

    /** 列代码 */
    private final String colCode;

    /** 列号 */
    private final int colNum;

    /**
     * 构造函数
     *
     * @param ref 引用
     */
    public ExcelCellRef(final String ref) {
        int colNum = 0;
        StringBuilder colCode = new StringBuilder();
        StringBuilder rowCode = new StringBuilder();

        for (char c: ref.toCharArray()) {
            if (Character.isAlphabetic(c)) {
                colCode.append(c);
                colNum = colNum * 26 + (c - 'A') + 1;
            } else if (Character.isDigit(c)) {
                rowCode.append(c);
            }
        }

        this.ref = ref;
        this.rowCode = rowCode.toString();
        this.rowNum = Integer.parseInt(rowCode.toString());
        this.colCode = colCode.toString();
        this.colNum = colNum;
    }

    /**
     * 构造函数
     *
     * @param rowNum 行号
     * @param colNum 单元格号
     */
    public ExcelCellRef(final int rowNum, final int colNum) {
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.rowCode = getRowCode();
        this.colCode = getColCode();
        this.ref = this.colCode + this.rowCode;
    }

    /**
     * 读取行代码
     *
     * @return 行代码
     */
    public String getRowCode() {
        if (StringUtil.isEmpty(rowCode)) {
            return String.valueOf(rowNum);
        }
        return rowCode;
    }

    /**
     * 读取单元格代码
     *
     * @return 单元格代码
     */
    public String getColCode() {
        if (StringUtil.isEmpty(colCode)) {
            int dividend = colNum;
            StringBuilder code = new StringBuilder();
            while (dividend > 0) {
                int remainder = dividend % 26;
                dividend = dividend / 26;

                if (remainder == 0) {
                    remainder = 26;
                    dividend = dividend - 1;
                }
                code.insert(0, Character.toString((char) remainder - 1 + 'A'));
            }
            return code.toString();
        }
        return colCode;
    }
}
