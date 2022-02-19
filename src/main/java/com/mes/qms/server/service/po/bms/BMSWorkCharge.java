package com.mes.qms.server.service.po.bms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BMSWorkCharge implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BMSWorkCharge() {
		// TODO Auto-generated constructor stub
	}

	public int ID;

	/**
	 * 工位ID 对应系统中的工段
	 */
	public int StationID = 0;

	public String StationName = "";

	public int AreaID = 0;

	public String AreaName = "";

	/**
	 * 部门ID 责任班组
	 */
	public int ClassID = 0;

	public String ClassName = "";

	/**
	 * 共同责任班组
	 */
	public List<Integer> DepartmentIDList = new ArrayList<Integer>();

	public String DepartmentName = "";

	/**
	 * 现场工艺人员
	 */
	public List<Integer> TechnicianList = new ArrayList<Integer>();

	public String TechnicianName = "";

	/**
	 * 是否激活
	 */
	public int Active = 0;

	public int CreatorID = 0;
	public String Creator = "";
	public Calendar CreateTime = Calendar.getInstance();
	public int EditorID = 0;
	public String Editor = "";
	public Calendar EditTime = Calendar.getInstance();

	public List<Integer> CheckerList = new ArrayList<Integer>();

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getCreatorID() {
		return CreatorID;
	}

	public void setCreatorID(int creatorID) {
		CreatorID = creatorID;
	}

	public String getCreator() {
		return Creator;
	}

	public void setCreator(String creator) {
		Creator = creator;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public int getEditorID() {
		return EditorID;
	}

	public void setEditorID(int editorID) {
		EditorID = editorID;
	}

	public String getEditor() {
		return Editor;
	}

	public void setEditor(String editor) {
		Editor = editor;
	}

	public Calendar getEditTime() {
		return EditTime;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
	}

	public int getClassID() {
		return ClassID;
	}

	public void setClassID(int classID) {
		ClassID = classID;
	}

	public String getClassName() {
		return ClassName;
	}

	public void setClassName(String className) {
		ClassName = className;
	}

	public int getStationID() {
		return StationID;
	}

	public void setStationID(int stationID) {
		StationID = stationID;
	}

	public String getStationName() {
		return StationName;
	}

	public void setStationName(String stationName) {
		StationName = stationName;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public int getAreaID() {
		return AreaID;
	}

	public String getAreaName() {
		return AreaName;
	}

	public List<Integer> getDepartmentIDList() {
		return DepartmentIDList;
	}

	public String getDepartmentName() {
		return DepartmentName;
	}

	public List<Integer> getTechnicianList() {
		return TechnicianList;
	}

	public String getTechnicianName() {
		return TechnicianName;
	}

	public List<Integer> getCheckerList() {
		return CheckerList;
	}

	public void setAreaID(int areaID) {
		AreaID = areaID;
	}

	public void setAreaName(String areaName) {
		AreaName = areaName;
	}

	public void setDepartmentIDList(List<Integer> departmentIDList) {
		DepartmentIDList = departmentIDList;
	}

	public void setDepartmentName(String departmentName) {
		DepartmentName = departmentName;
	}

	public void setTechnicianList(List<Integer> technicianList) {
		TechnicianList = technicianList;
	}

	public void setTechnicianName(String technicianName) {
		TechnicianName = technicianName;
	}

	public void setCheckerList(List<Integer> checkerList) {
		CheckerList = checkerList;
	}

}