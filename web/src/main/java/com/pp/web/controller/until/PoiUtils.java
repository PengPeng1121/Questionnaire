package com.pp.web.controller.until;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Poi工具类
 */
public class PoiUtils {

    // 日志
    Logger logger = LoggerFactory.getLogger(getClass());

    public final static String Excel_EnCode = "ISO8859_1";

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 生成excel文件
     * @param sheetName  文件名称
     * @param title      内部标题
     * @param columnName 列名
     * @param data       文件数据
     * @return HSSFWorkbook
     */
    public static HSSFWorkbook generateExcel(String sheetName, String title, String[] columnName, Object[][] data) {
        // 创建一个workbook 对应一个excel应用文件
        HSSFWorkbook workBook = new HSSFWorkbook();
        // 在workbook中添加一个sheet,对应Excel文件中的sheet

        // 如果没有给定sheet名，则默认使用Sheet1
        HSSFSheet sheet;
        if (org.apache.commons.lang3.StringUtils.isNotBlank(sheetName)) {
            sheet = workBook.createSheet(sheetName);
        } else {
            sheet = workBook.createSheet();
        }

        // 构建大标题，可以没有
        HSSFRow headRow = sheet.createRow(0);
        HSSFCell cell = null;
        cell = headRow.createCell(0);
        cell.setCellValue(title);

        //大标题行的偏移
        int offset = 0;
        if (org.apache.commons.lang3.StringUtils.isNotBlank(title)) {
            offset = 1;
        }

        // 构建列标题，不能为空
        headRow = sheet.createRow(offset);
        for (int i = 0; i < columnName.length; i++) {
            cell = headRow.createCell(i);
            cell.setCellValue(columnName[i]);
        }

        // 构建表体数据（二维数组），不能为空
        for (int i = 0; i < data.length; i++) {
            headRow = sheet.createRow(++offset);
            for (int j = 0; j < data[0].length; j++) {
                cell = headRow.createCell(j);
                if (data[i][j] instanceof BigDecimal)
                    cell.setCellValue(((BigDecimal) data[i][j]).doubleValue());
                else if (data[i][j] instanceof Double)
                    cell.setCellValue((Double) data[i][j]);
                else if (data[i][j] instanceof Long)
                    cell.setCellValue((Long) data[i][j]);
                else if (data[i][j] instanceof Integer)
                    cell.setCellValue((Integer) data[i][j]);
                else if (data[i][j] instanceof Boolean)
                    cell.setCellValue((Boolean) data[i][j]);
                else
                    cell.setCellValue((String) data[i][j]);
            }
        }
        return workBook;
    }

    /**
     * HSSFWorkbook 转换为 InputStream
     *
     * @param hssfWorkbook excel数据
     * @return InputStream 流
     */
    public static InputStream getInputStream(HSSFWorkbook hssfWorkbook) {
        InputStream is = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            hssfWorkbook.write(out);
            is = new ByteArrayInputStream(out.toByteArray());
            out.close();
        } catch (IOException e) {
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return is;
    }

    public static void export(String sheetName, String title, String[] columnName, Object[][] data, ServletOutputStream outputStream) {
        // 创建一个workbook 对应一个excel应用文件
        HSSFWorkbook workBook = new HSSFWorkbook();
        // 在workbook中添加一个sheet,对应Excel文件中的sheet

        // 如果没有给定sheet名，则默认使用Sheet1
        HSSFSheet sheet = null;
        if (StringUtils.isNotEmpty(sheetName))
            sheet = workBook.createSheet(sheetName);
        else
            sheet = workBook.createSheet();

        // 构建大标题，可以没有
        HSSFRow headRow = sheet.createRow(0);
        HSSFCell cell = null;
        cell = headRow.createCell(0);
        cell.setCellValue(title);

        //大标题行的偏移
        int offset = 0;
        if (StringUtils.isNotEmpty(title))
            offset = 1;

        // 构建列标题，不能为空
        headRow = sheet.createRow(offset);
        for (int i = 0; i < columnName.length; i++) {
            cell = headRow.createCell(i);
            cell.setCellValue(columnName[i]);
        }

        // 构建表体数据（二维数组），不能为空
        for (int i = 0; i < data.length; i++) {
            headRow = sheet.createRow(++offset);
            for (int j = 0; j < data[0].length; j++) {
                cell = headRow.createCell(j);

                if (data[i][j] instanceof BigDecimal)
                    cell.setCellValue(((BigDecimal) data[i][j]).doubleValue());
                else if (data[i][j] instanceof Double)
                    cell.setCellValue((Double) data[i][j]);
                else if (data[i][j] instanceof Long)
                    cell.setCellValue((Long) data[i][j]);
                else if (data[i][j] instanceof Integer)
                    cell.setCellValue((Integer) data[i][j]);
                else if (data[i][j] instanceof Boolean)
                    cell.setCellValue((Boolean) data[i][j]);
                else if (data[i][j] instanceof Date)
                    cell.setCellValue(sdf.format((Date) data[i][j]));
                else if (data[i][j] instanceof Calendar)
                    cell.setCellValue(sdf.format(((Calendar) data[i][j]).getTime()));
                else //if (data[i][j] instanceof String)
                    cell.setCellValue((String) data[i][j]);

            }
        }

        try {
            workBook.write(outputStream);
            outputStream.flush();
//            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null){
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}