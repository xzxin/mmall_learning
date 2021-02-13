package com.mmall.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * @ClassName: DateTimeUtil
 * @Description: 时间日期转换
 * @Author
 * @Date 2021/2/13
 * @Version 1.0
 */
public class DateTimeUtil {
  public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";
  
  public static Date strToDate(String dateTimeStr, String formatStr) {
    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
    DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
    return dateTime.toDate();
  }
  
  public static String dateToStr(Date date, String formatStr) {
    if (date == null) {
      return null;
    }
    DateTime dateTime = new DateTime(date);
    return dateTime.toString(formatStr);
  }
  
  public static Date strToDate(String dateTimeStr) {
    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
    DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
    return dateTime.toDate();
  }
  
  public static String dateToStr(Date date) {
    if (date == null) {
      return null;
    }
    DateTime dateTime = new DateTime(date);
    return dateTime.toString(STANDARD_FORMAT);
  }
}
