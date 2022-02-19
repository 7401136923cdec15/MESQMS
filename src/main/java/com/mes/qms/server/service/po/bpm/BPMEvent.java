package com.mes.qms.server.service.po.bpm;

import java.io.Serializable;
import java.util.Calendar;

public class BPMEvent implements Serializable {
	private static final long serialVersionUID = 1L;

	public int ID = 0;

	public int EventID = 0;

	public String EventName = "";

	public Calendar CreateTime = Calendar.getInstance();

	public int CreatorID = 0;

	public int FunctionID = 0; // 车间

	public String FunctionName = "";

	public String Creator = "";

	public int ModuleID = 0; // 职能分类()

	public String ModuleName = ""; // 职能文本

	public int Active = 0;

	public BPMEvent() {
		this.EventName = "";
		this.FunctionName = "";
		this.ModuleName = "";
		this.Creator = "";
		this.ModuleName = "";
		this.CreateTime = Calendar.getInstance();
		this.Active = 1;
	}

	public BPMEvent Clone() {
		BPMEvent wItem = new BPMEvent();
		wItem.ID = this.ID;
		wItem.CreatorID = this.CreatorID;
		wItem.EventID = this.EventID;
		wItem.FunctionID = this.FunctionID;
		wItem.ModuleID = this.ModuleID;

		wItem.EventName = this.EventName;
		wItem.FunctionName = this.FunctionName;
		wItem.ModuleName = this.ModuleName;
		wItem.CreateTime = this.CreateTime;
		wItem.Creator = this.Creator;
		wItem.Active = this.Active;
		return wItem;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getEventID() {
		return EventID;
	}

	public void setEventID(int eventID) {
		EventID = eventID;
	}

	public String getEventName() {
		return EventName;
	}

	public void setEventName(String eventName) {
		EventName = eventName;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public int getCreatorID() {
		return CreatorID;
	}

	public void setCreatorID(int creatorID) {
		CreatorID = creatorID;
	}

	public int getFunctionID() {
		return FunctionID;
	}

	public void setFunctionID(int functionID) {
		FunctionID = functionID;
	}

	public String getFunctionName() {
		return FunctionName;
	}

	public void setFunctionName(String functionName) {
		FunctionName = functionName;
	}

	public String getCreator() {
		return Creator;
	}

	public void setCreator(String creator) {
		Creator = creator;
	}

	public int getModuleID() {
		return ModuleID;
	}

	public void setModuleID(int moduleID) {
		ModuleID = moduleID;
	}

	public String getModuleName() {
		return ModuleName;
	}

	public void setModuleName(String moduleName) {
		ModuleName = moduleName;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}
}
