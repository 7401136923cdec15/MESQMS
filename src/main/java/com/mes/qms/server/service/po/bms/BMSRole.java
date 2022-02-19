package com.mes.qms.server.service.po.bms;

import java.io.Serializable;
import java.util.Calendar;

public class BMSRole implements Serializable {
	private static final long serialVersionUID = 1L;

	public int OwnerID = 0;

	public int ID = 0;

	public String Name = "";

	public String OwnerName = "";

	public String Explain = "";

	public Calendar CreateTime = Calendar.getInstance();

	public int Active = 0;

	public String ActiveText = "";

	public int getOwnerID() {
		return OwnerID;
	}

	public void setOwnerID(int ownerID) {
		OwnerID = ownerID;
	}

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

	public String getOwnerName() {
		return OwnerName;
	}

	public void setOwnerName(String ownerName) {
		OwnerName = ownerName;
	}

	public String getExplain() {
		return Explain;
	}

	public void setExplain(String explain) {
		Explain = explain;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public String getActiveText() {
		return ActiveText;
	}

	public void setActiveText(String activeText) {
		ActiveText = activeText;
	}

	public BMSRole() {
		this.OwnerID = 0;
		this.ID = 0;
		this.Name = "";
		this.OwnerName = "";
		this.Explain = "";
		this.ActiveText = "";
		this.CreateTime = Calendar.getInstance();
	}

	public void SetActiveText() {
		this.ActiveText = this.Active == 1 ? "启用" : this.Active == 2 ? "禁用" : "未使用";
	}
}
