package com.tc.calendar;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */
@SuppressLint("SimpleDateFormat")
public final class DateUtils {

	/**
     * Don't let anyone instantiate this class.
     */
    private DateUtils() {
        throw new Error("Do not need instantiate!");
    }
    
	// == ----------------------------------------- ==
    
	/** 日期格式类型 */
	public static final String yyyyMMdd = "yyyy-MM-dd";
	public static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
	public static final String yyyyMMddHHmmss_2 = "yyyy年M月d日 HH:mm:ss";
	public static final String HHmmss = "HH:mm:ss";
	public static final String hhmmMMDDyyyy = "hh:mm M月d日 yyyy";
	public static final String MMdd = "MM月dd日";

	/**
	 * 获取当前日期的字符串
	 * @param fString 日期格式，譬如："yyyy-MM-dd HH:mm:ss"
	 * @return 字符串
	 */
	public static String getDateNow(String fString) {
		try {
			if ((fString == null) || (fString.equals("")))
				fString = yyyyMMddHHmmss;

			Calendar cld = Calendar.getInstance();
			DateFormat df = new SimpleDateFormat(fString);
			return df.format(cld.getTime());
		} catch (Exception e) {
		}
		return null;
	}
	
	
	/**
	 * 将Date类型转换日期字符串
	 * @param date 日期
	 * @param fString 日期格式
	 * @return 按照需求格式的日期字符串
	 */
	public static String formatDate(Date date,String fString){
		try {
			return new SimpleDateFormat(fString).format(date);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 将日期字符串转换为Date类型
	 * @param dateStr
	 * @param pattern
	 * @return
	 */
	public static Date parseDate(String dateStr, String pattern){
		try {
			return new SimpleDateFormat(pattern).parse(dateStr);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 得到年
	 * @param date Date对象
	 * @return 年
	 */
	public static int getYear(Date date) {
		try {
			Calendar cld = Calendar.getInstance();
			cld.setTime(date);
			return cld.get(Calendar.YEAR);
		} catch (Exception e) {
		}
		return -1;
	}

	/**
	 * 得到月 (0 - 11) + 1
	 * @param date Date对象
	 * @return 月
	 */
	public static int getMonth(Date date) {
		try {
			Calendar cld = Calendar.getInstance();
			cld.setTime(date);
			return cld.get(Calendar.MONTH) + 1;
		} catch (Exception e) {
		}
		return -1;
	}

	/**
	 * 得到日
	 * @param date Date对象
	 * @return 日
	 */
	public static int getDay(Date date) {
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			return c.get(Calendar.DAY_OF_MONTH);
		} catch (Exception e) {
		}
		return -1;
	}
	
	/**
	 * 获取年
	 */
	public static String getYear() {
        return Calendar.getInstance().get(Calendar.YEAR) + "";
    }
 
	/**
	 * 获取月
	 */
    public static String getMonth() {
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        return month + "";
    }
 
    /**
     * 获取日
     */
    public static String getDay() {
        return Calendar.getInstance().get(Calendar.DATE) + "";
    }
 
    /**
     * 获取时
     */
    public static String get24Hour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + "";
    }
 
    /**
     * 获取分
     */
    public static String getMinute() {
        return Calendar.getInstance().get(Calendar.MINUTE) + "";
    }
 
    /**
     * 获取秒
     * @return
     */
    public static String getSecond() {
        return Calendar.getInstance().get(Calendar.SECOND) + "";
    }


	/**
	 * 时间戳转换成普通时间
	 * @return
	 */
	public static String getUnixToNormal(String time) {
		return new SimpleDateFormat(yyyyMMddHHmmss).format(new Date(Long.parseLong(time) *1000));
	}
	

}
