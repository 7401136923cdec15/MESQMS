package com.mes.qms.server.service.po.aps;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 任务版本(暂时不用)
 * 
 * @author ShrisJava
 *
 */
public class APSTaskVersion implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int ID = 0;
	public String VersionNo = "";
	public Calendar VersionDate = Calendar.getInstance();
	public int TaskID;
	public int TaskType;

	public static class APSVersion {
		public String VersionNo = "";
		public Calendar VersionDate = Calendar.getInstance();
		public int TaskType;
	}
}
