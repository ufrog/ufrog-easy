package net.ufrog.easy.offices.poi.excel;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.utils.StringUtil;

import java.io.Serial;
import java.io.Serializable;

/**
 * 表格片
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-16
 * @since 3.5.3
 */
@Slf4j
@Getter
@Setter
public class ExcelSheet implements Serializable {

    @Serial
    private static final long serialVersionUID = -3789923025872466599L;

    /** 索引 */
    private int index;

    /** 名称 */
    private String name;

    /** 表格工作簿 */
    private ExcelWorkbook workbook;

    /**
     * 检查是否匹配
     *
     * @param index 索引
     * @param name 名称
     * @return 检查结果
     */
    public boolean check(int index, String name) {
        if (!StringUtil.isEmpty(this.name) && StringUtil.equals(this.name, name)) {
            this.index = index;
            return true;
        } else if (StringUtil.isEmpty(this.name) && this.index == index) {
            this.name = name;
            return true;
        }
        return false;
    }

    /**
     * 新建构造器
     *
     * @return 构造器
     */
    public static ExcelSheetBuilder builder() {
        return new ExcelSheetBuilder();
    }

    /**
     * 表格片构建器
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.5.3, 2025-11-23
     * @since 3.5.3
     */
    public static final class ExcelSheetBuilder {

        /** 索引 */
        private int index = 0;

        /** 名称 */
        private String name;

        /**
         * 设置索引
         *
         * @param index 索引
         * @return 表格片构建器
         */
        public ExcelSheetBuilder index(int index) {
            this.index = index;
            return this;
        }

        /**
         * 设置名称
         *
         * @param name 名称
         * @return 表格片构建器
         */
        public ExcelSheetBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * 构建
         *
         * @return 表格片
         */
        public ExcelSheet build() {
            ExcelSheet excelSheet = new ExcelSheet();
            excelSheet.index = this.index;
            excelSheet.name = name;
            return excelSheet;
        }
    }
}
