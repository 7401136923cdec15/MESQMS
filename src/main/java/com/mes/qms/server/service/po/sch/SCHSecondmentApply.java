package com.mes.qms.server.service.po.sch;

import java.io.Serializable; 
import java.util.Calendar; 

import com.mes.qms.server.service.po.bpm.BPMTaskBase;
 

/**
 * 人员借调单
 * 
 * @author PengYouWang
 * @CreateTime 2020年1月6日10:04:55
 * @LastEditTime 2020年1月6日22:24:46
 *
 */
public class SCHSecondmentApply  extends BPMTaskBase implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 流程类型(1、跨工区跨班组借调 2、本工区跨班组借调 )
	 */
	public int Type = 0;
	/**
	 * 调入人员ID （Step1，Type1,2,3）
	 */
	public int PersonID = 0;
	/**
	 * 原属班组（Step1，Type1,2）
	 */
	public int OldClassID = 0;
	/**
	 * 调入班组（Step1，Type1,2）
	 */
	public int NewClassID = 0;
	/**
	 * 原属工位 （Step1，Type3）
	 */
	public String OldPartList = "";
	/**
	 * 调入工位 （Step1，Type3）
	 */
	public String NewPartList = "";
	/**
	 * 原属岗位（Step1，Type1,2,3）
	 */
	public String OldPosition = "";
	/**
	 * 调入岗位（Step1，Type1,2,3）
	 */
	public String NewPosition = "";
	/**
	 * 调动开始时间（Step1，Type1,2,3）
	 */
	public Calendar StartTime = Calendar.getInstance();
	/**
	 * 调动 截止时间（Step1，Type1,2,3）
	 */
	public Calendar EndTime = Calendar.getInstance();

	/**
	 * 工区ID
	 */
	public int AreaID = 0;
	/**
	 * 工区名称
	 */
	public String AreaName = "";
	/**
	 * 调入工区ID
	 */
	public int NewAreaID = 0;
	/**
	 * 调入工区名称
	 */
	public String NewAreaName = "";

	// 辅助属性
	public String PersonName = "";
	public String OldClassName = "";
	public String NewClassName = "";
	public String TypeText = "";
	public String OldPartNames = "";
	public String NewPartNames = "";

	public SCHSecondmentApply() {
		CreateTime.set(2000, 0, 1, 0, 0, 0);
		SubmitTime.set(2000, 0, 1, 0, 0, 0);
		StartTime.set(2000, 0, 1, 0, 0, 0);
		EndTime.set(2000, 0, 1, 0, 0, 0);
	}

	  

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public int getPersonID() {
		return PersonID;
	}

	public void setPersonID(int personID) {
		PersonID = personID;
	}

	public int getOldClassID() {
		return OldClassID;
	}

	public void setOldClassID(int oldClassID) {
		OldClassID = oldClassID;
	}

	public int getNewClassID() {
		return NewClassID;
	}

	public void setNewClassID(int newClassID) {
		NewClassID = newClassID;
	}

	public Calendar getStartTime() {
		return StartTime;
	}

	public void setStartTime(Calendar startTime) {
		StartTime = startTime;
	}

	public Calendar getEndTime() {
		return EndTime;
	}

	public void setEndTime(Calendar endTime) {
		EndTime = endTime;
	}

	public String getPersonName() {
		return PersonName;
	}

	public void setPersonName(String personName) {
		PersonName = personName;
	}

	public String getOldClassName() {
		return OldClassName;
	}

	public void setOldClassName(String oldClassName) {
		OldClassName = oldClassName;
	}

	public String getNewClassName() {
		return NewClassName;
	}

	public void setNewClassName(String newClassName) {
		NewClassName = newClassName;
	}

	public String getTypeText() {
		return TypeText;
	}

	public void setTypeText(String typeText) {
		TypeText = typeText;
	}

	public String getOldPartList() {
		return OldPartList;
	}

	public void setOldPartList(String oldPartList) {
		OldPartList = oldPartList;
	}

	public String getNewPartList() {
		return NewPartList;
	}

	public void setNewPartList(String newPartList) {
		NewPartList = newPartList;
	}

	public String getOldPosition() {
		return OldPosition;
	}

	public void setOldPosition(String oldPosition) {
		OldPosition = oldPosition;
	}

	public String getNewPosition() {
		return NewPosition;
	}

	public void setNewPosition(String newPosition) {
		NewPosition = newPosition;
	}

	public String getOldPartNames() {
		return OldPartNames;
	}

	public void setOldPartNames(String oldPartNames) {
		OldPartNames = oldPartNames;
	}

	public String getNewPartNames() {
		return NewPartNames;
	}

	public void setNewPartNames(String newPartNames) {
		NewPartNames = newPartNames;
	}

	public int getAreaID() {
		return AreaID;
	}

	public void setAreaID(int areaID) {
		AreaID = areaID;
	}

	public String getAreaName() {
		return AreaName;
	}

	public void setAreaName(String areaName) {
		AreaName = areaName;
	}

	public int getNewAreaID() {
		return NewAreaID;
	}

	public void setNewAreaID(int newAreaID) {
		NewAreaID = newAreaID;
	}

	public String getNewAreaName() {
		return NewAreaName;
	}

	public void setNewAreaName(String newAreaName) {
		NewAreaName = newAreaName;
	}
}
