package com.mes.qms.server.service.po.sfc;

import java.io.Serializable;
import java.util.Calendar;

import com.mes.qms.server.service.po.aps.APSTaskStep;

/**
 * 打卡记录
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-2-21 13:22:45
 * @LastEditTime 2020-2-21 13:22:48
 *
 */
public class SFCLoginEvent implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 车间ID
	 */
	public int WorkShopID = 0;
	/**
	 * 工位ID
	 */
	public int StationID = 0;
	/**
	 * 操作员ID
	 */
	public int OperatorID = 0;
	/**
	 * 生产、质量、工艺、设备、仓库
	 */
	public int ModuleID = 0;
	/**
	 * 当前班次
	 */
	public int ShiftID = 0;
	/**
	 * 激活状态
	 */
	public int Active = 0;
	/**
	 * 打卡时刻
	 */
	public Calendar LoginTime = Calendar.getInstance();
	/**
	 * 打卡类型：上班|下班
	 */
	public int Type = 0;

	// 广机
	/**
	 * 工序任务ID
	 */
	public int SFCTaskStepID = 0;
	/**
	 * 备注
	 */
	public String Remark = "";

	// 辅助属性
	public APSTaskStep APSTaskStep = new APSTaskStep();
	/**
	 * 车间名称
	 */
	public String WorkShopName = "";
	/**
	 * 操作员名称
	 */
	public String OperatorName = "";
	/**
	 * 模块名称
	 */
	public String ModuleName = "";
	/**
	 * 工位名称
	 */
	public String StationName = "";
	/**
	 * 打卡说明
	 */
	public String LoginText = "";

	public SFCLoginEvent() {
		this.ID = 0;
		this.WorkShopID = 0;
		this.StationID = 0;
		this.OperatorID = 0;
		this.ModuleID = 0;
		this.ShiftID = 0;
		this.Type = 0;
		this.Active = 0;

		this.LoginTime = Calendar.getInstance();
		this.WorkShopName = "";
		this.OperatorName = "";
		this.ModuleName = "";
		this.StationName = "";
		this.LoginText = "";
	}

	public SFCLoginEvent(int wLoginID, int wWorkShopID, int wStationID, int wModuleID, int wLoginType) {
		this.LoginTime = Calendar.getInstance();
		this.WorkShopName = "";
		this.OperatorName = "";
		this.ModuleName = "";
		this.OperatorID = wLoginID;
		this.WorkShopID = wWorkShopID;
		this.StationID = wStationID;
		this.ModuleID = wModuleID;
		this.Type = wLoginType;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getWorkShopID() {
		return WorkShopID;
	}

	public void setWorkShopID(int workShopID) {
		WorkShopID = workShopID;
	}

	public int getStationID() {
		return StationID;
	}

	public void setStationID(int stationID) {
		StationID = stationID;
	}

	public int getOperatorID() {
		return OperatorID;
	}

	public void setOperatorID(int operatorID) {
		OperatorID = operatorID;
	}

	public int getModuleID() {
		return ModuleID;
	}

	public void setModuleID(int moduleID) {
		ModuleID = moduleID;
	}

	public int getShiftID() {
		return ShiftID;
	}

	public void setShiftID(int shiftID) {
		ShiftID = shiftID;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public Calendar getLoginTime() {
		return LoginTime;
	}

	public void setLoginTime(Calendar loginTime) {
		LoginTime = loginTime;
	}

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public String getWorkShopName() {
		return WorkShopName;
	}

	public void setWorkShopName(String workShopName) {
		WorkShopName = workShopName;
	}

	public String getOperatorName() {
		return OperatorName;
	}

	public void setOperatorName(String operatorName) {
		OperatorName = operatorName;
	}

	public String getModuleName() {
		return ModuleName;
	}

	public void setModuleName(String moduleName) {
		ModuleName = moduleName;
	}

	public String getStationName() {
		return StationName;
	}

	public void setStationName(String stationName) {
		StationName = stationName;
	}

	public String getLoginText() {
		return LoginText;
	}

	public void setLoginText(String loginText) {
		LoginText = loginText;
	}

	public int getSFCTaskStepID() {
		return SFCTaskStepID;
	}

	public void setSFCTaskStepID(int sFCTaskStepID) {
		SFCTaskStepID = sFCTaskStepID;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public APSTaskStep getAPSTaskStep() {
		return APSTaskStep;
	}

	public void setAPSTaskStep(APSTaskStep aPSTaskStep) {
		APSTaskStep = aPSTaskStep;
	}
}
