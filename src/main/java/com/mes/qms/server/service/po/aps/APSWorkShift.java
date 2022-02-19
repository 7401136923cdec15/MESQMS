package com.mes.qms.server.service.po.aps;

import java.io.Serializable;
import java.util.Calendar;

public class APSWorkShift implements Serializable {
	private static final long serialVersionUID = 1L;
	// [DataMember(Name = "ID", Order = 0)]
	public int ID = 0;
	// [DataMember(Name = "WeekDay", Order = 1)]
	public int WeekDay = 0; // 周几
	// [DataMember(Name = "ShiftID", Order = 2)]
	public int ShiftID = 0; // 白班、夜班ID
	// [DataMember(Name = "ShiftName", Order = 3)]
	public String ShiftName = ""; // 班名称
	// [DataMember(Name = "WorkDate", Order = 4)]
	public Calendar WorkDate = Calendar.getInstance(); // 工作日
	// [DataMember(Name = "Active", Order = 5)]
	public int Active = 0; // 是否生产
	// [DataMember(Name = "WorkHours", Order = 6)]
	public int WorkHours = 0; // 工作时长

	public APSWorkShift() {
		this.ShiftName = "";
		this.WorkDate = Calendar.getInstance();
		this.WeekDay = this.WorkDate.get(Calendar.DAY_OF_WEEK);
		this.ID = 0;
		this.ShiftID = 0;
		this.WorkHours = 0;
		this.Active = 1;
	}

	public APSWorkShift(int wID, Calendar wWorkDate) {
		this.ID = wID;
		this.WorkDate = wWorkDate;
		this.WeekDay = this.WorkDate.get(Calendar.DAY_OF_WEEK);
		this.Active = 1;
		this.ShiftID = 0;
		this.ShiftName = "";
		this.WorkHours = 0;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getWeekDay() {
		return WeekDay;
	}

	public void setWeekDay(int weekDay) {
		WeekDay = weekDay;
	}

	public int getShiftID() {
		return ShiftID;
	}

	public void setShiftID(int shiftID) {
		ShiftID = shiftID;
	}

	public String getShiftName() {
		return ShiftName;
	}

	public void setShiftName(String shiftName) {
		ShiftName = shiftName;
	}

	public Calendar getWorkDate() {
		return WorkDate;
	}

	public void setWorkDate(Calendar workDate) {
		WorkDate = workDate;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public int getWorkHours() {
		return WorkHours;
	}

	public void setWorkHours(int workHours) {
		WorkHours = workHours;
	}
}
