package net.ufrog.easy.offices.poi.excel;

import net.ufrog.easy.exceptions.CommonException;
import net.ufrog.easy.utils.ObjectUtil;
import org.aspectj.bridge.Message;

import java.io.Serial;

/**
 * 表格异常
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-23
 * @since 3.5.3
 */
public class ExcelException extends CommonException {

    @Serial
    private static final long serialVersionUID = -7430699025547911713L;

    /** 表格片 */
    private final ExcelSheet excelSheet;

    /** 行号 */
    private final int rowNum;

    /** 列号 */
    private final int colNum;

    /**
     * 构造函数
     *
     * @param excelSheet 表格片
     * @param rowNum 行号
     * @param colNum 列号
     * @param message 消息
     * @param cause 原因
     */
    public ExcelException(ExcelSheet excelSheet, int rowNum, int colNum, String message, Throwable cause) {
        super(message, cause);
        setMessageKey("common.exception.excel");

        this.excelSheet = excelSheet;
        this.rowNum = rowNum;
        this.colNum = colNum;
    }

    @Override
    public String getMessage() {
        StringBuilder builder = new StringBuilder("Failed to read excel file");
        ObjectUtil.ifPresent(excelSheet, v -> builder.append(", sheet: ").append(excelSheet.getIndex()).append(" - ").append(excelSheet.getName()));
        ObjectUtil.ifTrueOrElse(rowNum >= 0, () -> builder.append(", row: ").append(rowNum), null);
        ObjectUtil.ifTrueOrElse(colNum >= 0, () -> builder.append(", col: ").append(colNum), null);
        ObjectUtil.ifPresent(super.getMessage(), v -> builder.append(", error message: ").append(super.getMessage()));
        ObjectUtil.ifPresent(getCause(), v -> builder.append(", cause: ").append(getCause().getMessage()));
        return builder.append(".").toString();
    }
}
