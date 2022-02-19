package com.mes.qms.server.service.po.bms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BMSPosition implements Serializable {

	private static final long serialVersionUID = 1L;

	public int ID;

	public String Name = "";

	public int Active = 0;

	public int ParentID = 0;

	public int DepartmentID = 0;

	public int DutyID = 0;

	public int OperatorID = 0;

	public String Operator = "";

	public Calendar EditTime = Calendar.getInstance();

	public List<BMSPosition> SonList = new ArrayList<>();

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public int getParentID() {
		return ParentID;
	}

	public void setParentID(int parentID) {
		ParentID = parentID;
	}

	public int getDepartmentID() {
		return DepartmentID;
	}

	public void setDepartmentID(int departmentID) {
		DepartmentID = departmentID;
	}

	public int getDutyID() {
		return DutyID;
	}

	public void setDutyID(int dutyID) {
		DutyID = dutyID;
	}

	public List<BMSPosition> getSonList() {
		return SonList;
	}

	public void setSonList(List<BMSPosition> sonList) {
		SonList = sonList;
	}

	public BMSPosition() {
		this.ID = -1;
		this.Name = "";
		this.SonList = new ArrayList<>();
		this.Active = 1;
		this.OperatorID = 0;
		this.Operator = "";
		this.EditTime = Calendar.getInstance();
		this.ParentID = 0;
		this.DepartmentID = 0;
	}

	public int getOperatorID() {
		return OperatorID;
	}

	public void setOperatorID(int operatorID) {
		OperatorID = operatorID;
	}

	public String getOperator() {
		return Operator;
	}

	public void setOperator(String operator) {
		Operator = operator;
	}

	public Calendar getEditTime() {
		return EditTime;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
	}
}
