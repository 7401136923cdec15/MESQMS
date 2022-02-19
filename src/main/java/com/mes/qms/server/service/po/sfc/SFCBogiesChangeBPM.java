package com.mes.qms.server.service.po.sfc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.mes.qms.server.service.po.bpm.BPMTaskBase;

/**
 * 转向架互换
 */
public class SFCBogiesChangeBPM extends BPMTaskBase implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 原始订单ID
	 */
	public int SOrderID = 0;
	/**
	 * 目标订单ID
	 */
	public int TOrderID = 0;
	/**
	 * 响应等级
	 */
	public int RespondLevel = 0;
	public String RespondLevelName = "";

	// ①辅助属性
	public String SProductNo = "";
	public String SLineName = "";
	public String SCustomerName = "";
	public String SPartNo = "";

	public String TProductNo = "";
	public String TLineName = "";
	public String TCustomerName = "";
	public String TPartNo = "";

	/**
	 * 异常类型
	 */
	public int ExceptionType = 0;
	public String ExceptionTypeName = "";
	/**
	 * 责任部门
	 */
	public int DutyDepartmentID = 0;
	public String DutyDepartmentName = "";
	/**
	 * 发生工位
	 */
	public int OccurPartID = 0;
	public String OccurPartName = "";
	/**
	 * 解决期限
	 */
	public Calendar SolveDeadLineTime = Calendar.getInstance();

	/**
	 * 子项列表
	 */
	public List<SFCBogiesChangeBPMItem> ItemList = new ArrayList<SFCBogiesChangeBPMItem>();

	public int TagTypes = 0;

	public int getSOrderID() {
		return SOrderID;
	}

	public int getTOrderID() {
		return TOrderID;
	}

	public String getSProductNo() {
		return SProductNo;
	}

	public String getSLineName() {
		return SLineName;
	}

	public List<SFCBogiesChangeBPMItem> getItemList() {
		return ItemList;
	}

	public void setItemList(List<SFCBogiesChangeBPMItem> itemList) {
		ItemList = itemList;
	}

	public int getTagTypes() {
		return TagTypes;
	}

	public void setTagTypes(int tagTypes) {
		TagTypes = tagTypes;
	}

	public String getSCustomerName() {
		return SCustomerName;
	}

	public int getRespondLevel() {
		return RespondLevel;
	}

	public String getRespondLevelName() {
		return RespondLevelName;
	}

	public void setRespondLevelName(String respondLevelName) {
		RespondLevelName = respondLevelName;
	}

	public void setRespondLevel(int respondLevel) {
		RespondLevel = respondLevel;
	}

	public String getSPartNo() {
		return SPartNo;
	}

	public String getTProductNo() {
		return TProductNo;
	}

	public String getTLineName() {
		return TLineName;
	}

	public String getTCustomerName() {
		return TCustomerName;
	}

	public String getTPartNo() {
		return TPartNo;
	}

	public void setSOrderID(int sOrderID) {
		SOrderID = sOrderID;
	}

	public void setTOrderID(int tOrderID) {
		TOrderID = tOrderID;
	}

	public void setSProductNo(String sProductNo) {
		SProductNo = sProductNo;
	}

	public void setSLineName(String sLineName) {
		SLineName = sLineName;
	}

	public int getExceptionType() {
		return ExceptionType;
	}

	public String getExceptionTypeName() {
		return ExceptionTypeName;
	}

	public int getDutyDepartmentID() {
		return DutyDepartmentID;
	}

	public String getDutyDepartmentName() {
		return DutyDepartmentName;
	}

	public int getOccurPartID() {
		return OccurPartID;
	}

	public String getOccurPartName() {
		return OccurPartName;
	}

	public Calendar getSolveDeadLineTime() {
		return SolveDeadLineTime;
	}

	public void setExceptionType(int exceptionType) {
		ExceptionType = exceptionType;
	}

	public void setExceptionTypeName(String exceptionTypeName) {
		ExceptionTypeName = exceptionTypeName;
	}

	public void setDutyDepartmentID(int dutyDepartmentID) {
		DutyDepartmentID = dutyDepartmentID;
	}

	public void setDutyDepartmentName(String dutyDepartmentName) {
		DutyDepartmentName = dutyDepartmentName;
	}

	public void setOccurPartID(int occurPartID) {
		OccurPartID = occurPartID;
	}

	public void setOccurPartName(String occurPartName) {
		OccurPartName = occurPartName;
	}

	public void setSolveDeadLineTime(Calendar solveDeadLineTime) {
		SolveDeadLineTime = solveDeadLineTime;
	}

	public void setSCustomerName(String sCustomerName) {
		SCustomerName = sCustomerName;
	}

	public void setSPartNo(String sPartNo) {
		SPartNo = sPartNo;
	}

	public void setTProductNo(String tProductNo) {
		TProductNo = tProductNo;
	}

	public void setTLineName(String tLineName) {
		TLineName = tLineName;
	}

	public void setTCustomerName(String tCustomerName) {
		TCustomerName = tCustomerName;
	}

	public void setTPartNo(String tPartNo) {
		TPartNo = tPartNo;
	}
}
