package com.mes.qms.server.service.po.sch;

import java.io.Serializable;
import java.util.Calendar;

public class SCHTemplate implements Serializable {
	private static final long serialVersionUID = 1L;

	public int ID = 0;

	public String Name;

	public Calendar CreateTime = Calendar.getInstance();

	public int CreatorID = 0;

	public Calendar EditTime = Calendar.getInstance();

	public int EditorID = 0;

	public int LineID = 0;

	public int WorkShopID = 0;

	public String Factory = "";

	public String BusinessUnit = "";

	public String WorkShop = "";

	public String Line = "";

	public String Creator = "";

	public String Editor = "";

	public int ModuleID = 0; // 职能类别

	public String ModuleName = ""; // 职能类别名称

	public int Active = 0; // 状态

	public SCHTemplate() {
		this.Name = "";
		this.ModuleName = "";
		this.Factory = "";
		this.BusinessUnit = "";
		this.Editor = "";
		this.Creator = "";
		this.CreateTime = Calendar.getInstance();
		this.EditTime = Calendar.getInstance();
	}

	public SCHTemplate Clone() {
		SCHTemplate wItem = new SCHTemplate();
		wItem.ID = this.ID;
		wItem.Name = this.Name;

		wItem.CreatorID = this.CreatorID;
		wItem.EditorID = this.EditorID;
		wItem.CreateTime = this.CreateTime;
		wItem.EditTime = this.EditTime;

		wItem.LineID = this.LineID;
		wItem.WorkShopID = this.WorkShopID;
		wItem.Factory = this.Factory;
		wItem.BusinessUnit = this.BusinessUnit;
		wItem.WorkShop = this.WorkShop;
		wItem.Line = this.Line;
		wItem.Creator = this.Creator;
		wItem.Editor = this.Editor;
		return wItem;
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

	public Calendar getEditTime() {
		return EditTime;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
	}

	public int getEditorID() {
		return EditorID;
	}

	public void setEditorID(int editorID) {
		EditorID = editorID;
	}

	public int getLineID() {
		return LineID;
	}

	public void setLineID(int lineID) {
		LineID = lineID;
	}

	public int getWorkShopID() {
		return WorkShopID;
	}

	public void setWorkShopID(int workShopID) {
		WorkShopID = workShopID;
	}

	public String getFactory() {
		return Factory;
	}

	public void setFactory(String factory) {
		Factory = factory;
	}

	public String getBusinessUnit() {
		return BusinessUnit;
	}

	public void setBusinessUnit(String businessUnit) {
		BusinessUnit = businessUnit;
	}

	public String getWorkShop() {
		return WorkShop;
	}

	public void setWorkShop(String workShop) {
		WorkShop = workShop;
	}

	public String getLine() {
		return Line;
	}

	public void setLine(String line) {
		Line = line;
	}

	public String getCreator() {
		return Creator;
	}

	public void setCreator(String creator) {
		Creator = creator;
	}

	public String getEditor() {
		return Editor;
	}

	public void setEditor(String editor) {
		Editor = editor;
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
