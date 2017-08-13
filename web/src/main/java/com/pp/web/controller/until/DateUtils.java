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
 * 时间工具类
 */
public class DateUtils {

    public final static String format_yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取两个时间的时间差 如x天x小时，如果超过一年，则返回x年x天x小时
     * 86400000 = 1000 * 24 * 60 * 60;
     * 3600000 = 1000 * 60 * 60;
     *
     * @param endDate   结束时间
     * @param beginDate 开始时间
     * @return 差值字符串
     */
    public static String getDateDuration(Date endDate, Date beginDate) {
        if (null == endDate || null == beginDate) {
            return org.apache.commons.lang3.StringUtils.EMPTY;
        }
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - beginDate.getTime();
        // 取绝对值
        if (diff < 0) {
            diff = -diff;
        }
        // 计算差多少天
        long day = diff / 86400000;
        // 计算差多少小时
        long hour = diff % 86400000 / 3600000;
        // 差多少年
        long year = 0;
        if (day >= 365) {
            year = day / 365;
            day = day % 365;
        }
        if (year > 0) {
            return year + "年" + day + "天" + hour + "小时";
        }
        return day + "天" + hour + "小时";
    }

    public static Date timeStamp2Date(String seconds) {
        return new Date(Long.valueOf(seconds));
    }


    public static String date2TimeStamp(String date_str,String format){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date_str).getTime()/1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}