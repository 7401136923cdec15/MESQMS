package com.mes.qms.server.service.po.cfg;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 单位
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-5-10 11:13:50
 * @LastEditTime 2020-5-10 11:13:55
 *
 */
public class CFGUnit implements Serializable {

	private static final long serialVersionUID = 1L;

	public int Active = 0;

	public int ERPUnitID = 0;

	public Calendar EditTime = Calendar.getInstance();

	public int GroupID = 0;

	public String GroupText = "";

	public int ID = 0;

	public String Name = "";

	public String Operator = "";

	public int OperatorID = 0;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public int getERPUnitID() {
		return ERPUnitID;
	}

	public void setERPUnitID(int eRPUnitID) {
		ERPUnitID = eRPUnitID;
	}

	public Calendar getEditTime() {
		return EditTime;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
	}

	public int getGroupID() {
		return GroupID;
	}

	public void setGroupID(int groupID) {
		GroupID = groupID;
	}

	public String getGroupText() {
		return GroupText;
	}

	public void setGroupText(String groupText) {
		GroupText = groupText;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getOperator() {
		return Operator;
	}

	public void setOperator(String operator) {
		Operator = operator;
	}

	public int getOperatorID() {
		return OperatorID;
	}

	public void setOperatorID(int operatorID) {
		OperatorID = operatorID;
	}

	public CFGUnit Clone() {
		CFGUnit wItem = new CFGUnit();
		wItem.ID = this.ID;
		return wItem;
	}
}
