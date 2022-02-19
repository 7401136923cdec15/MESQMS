package com.mes.qms.server.service.po.cfg;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 
 * @author PengYouWang
 * @CreateTime 2019年12月29日14:55:43
 * @LastEditTime 2019年12月29日14:55:47
 *
 */
public class CFGCalendar implements Serializable {

	private static final long serialVersionUID = 1L;

	public int OperatorID;
	public Calendar OperationTime = Calendar.getInstance();
	public int Year;
	public int FactoryID;
	public int WorkShopID;
	public int LineID;
	public String OperatorName = "";
	public int Active;
	public Calendar HolidayDate = Calendar.getInstance();

	public CFGCalendar() {
		OperationTime.set(2000, 1, 1);
		HolidayDate.set(2000, 1, 1);
	}

	public int getOperatorID() {
		return OperatorID;
	}

	public void setOperatorID(int operatorID) {
		OperatorID = operatorID;
	}

	public Calendar getOperationTime() {
		return OperationTime;
	}

	public void setOperationTime(Calendar operationTime) {
		OperationTime = operationTime;
	}

	public int getYear() {
		return Year;
	}

	public void setYear(int year) {
		Year = year;
	}

	public int getFactoryID() {
		return FactoryID;
	}

	public void setFactoryID(int factoryID) {
		FactoryID = factoryID;
	}

	public int getWorkShopID() {
		return WorkShopID;
	}

	public void setWorkShopID(int workShopID) {
		WorkShopID = workShopID;
	}

	public int getLineID() {
		return LineID;
	}

	public void setLineID(int lineID) {
		LineID = lineID;
	}

	public String getOperatorName() {
		return OperatorName;
	}

	public void setOperatorName(String operatorName) {
		OperatorName = operatorName;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public Calendar getHolidayDate() {
		return HolidayDate;
	}

	public void setHolidayDate(Calendar holidayDate) {
		HolidayDate = holidayDate;
	}
}
