package com.mes.qms.server.service.utils;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calendar工具类
 * 
 * @author PengYouWang
 * @CreateTime 2020年1月4日14:37:59
 * @LastEditTime 2020年1月4日14:38:05
 *
 */
public class CalendarUtil {

	private static Logger logger = LoggerFactory.getLogger(CalendarUtil.class);

	private CalendarUtil() {
	}

	/**
	 * 获取日期部分
	 * 
	 * @param wCalenar
	 * @return
	 */
	public static Calendar GetDate(Calendar wCalenar) {
		Calendar wResult = Calendar.getInstance();
		try {
			int wYear = wCalenar.get(Calendar.YEAR);
			int wMonth = wCalenar.get(Calendar.MONTH);
			int wDate = wCalenar.get(Calendar.DATE);
			int wHour = 0;
			int wMinute = 0;
			int wSecond = 0;
			wResult.set(wYear, wMonth, wDate, wHour, wMinute, wSecond);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}
}
