package com.mes.qms.server.service.po.rpt;

import java.util.Calendar;

public class RPTOrderPart {
	public int OrderID = 0;
	/**
	 * 修程
	 */
	public int LineID;

	public String Line = "";

	public int CustomerID = 0;

	public String CustomerName = "";

	public String PartNo = "";

	public int PartID = 0;

	public String PartName = "";

	/**
	 * 月计划完工日期
	 */
	public Calendar PlantDate = Calendar.getInstance();

	/**
	 * 实际完工日期
	 */
	public Calendar RealDate = Calendar.getInstance();

	/**
	 * 延迟天数
	 */
	public int LaterDay = 0;
}
