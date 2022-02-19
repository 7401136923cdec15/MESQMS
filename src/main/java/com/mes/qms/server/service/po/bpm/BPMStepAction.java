package com.mes.qms.server.service.po.bpm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.mes.qms.server.service.mesenum.BPMFlowTypes;
import com.mes.qms.server.service.mesenum.BPMResultEnum;

/**
 * 流程步骤操作表
 * 
 * @author ShrisJava
 *
 */
public class BPMStepAction implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID;

	/**
	 * 任务ID
	 */
	public int BaseID = 0;

	/**
	 * 流程枚举
	 */
	public int FlowType = BPMFlowTypes.Default.getValue();

	/**
	 * 流程步骤配置 主键ID
	 */
	public int StepID;

	/**
	 * 流程步骤名称 不存
	 */
	public String StepName = "";

	/**
	 * 流程步骤枚举 不存
	 */
	public int StepEnum = 0;

	/**
	 * 步骤类型 跟存数据相关 【暂不用 保留字段】 不存
	 */
	public int StepType = 0;

	/**
	 * 流程结果 （如：0-未操作 1：OK 2:NotOK ）
	 */
	public int Result = BPMResultEnum.Undo.getValue();

	public String Remark = "";

	/**
	 * 图片路径
	 */
	public List<String> FilePath = new ArrayList<String>();

	/**
	 * 步骤需填充字段
	 */
	public List<BPMCustom> CustomList = new ArrayList<>();

	/**
	 * 提交人
	 */
	public int SubmitorID = 0;

	/**
	 * 提交人
	 */
	public String SubmitorName = "";

	/**
	 * 
	 */
	public List<Integer> NextStepEmployeeID = new ArrayList<>();

	/**
	 * 提交时间
	 */
	public Calendar SubmitTime = Calendar.getInstance();

	/**
	 * 提交人附加信息 (如： {部门}-{岗位})
	 */
	public String SubmitorInfo = "";

	/**
	 * 是否启用
	 */
	public int Active = 0;

	public Boolean IsAllow = false;
	

	public int RoleID = 0;

	public BPMStepAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getSubmitorName() {
		return SubmitorName;
	}

	public void setSubmitorName(String submitorName) {
		SubmitorName = submitorName;
	}

	public List<Integer> getNextStepEmployeeID() {
		return NextStepEmployeeID;
	}

	public void setNextStepEmployeeID(List<Integer> nextStepEmployeeID) {
		NextStepEmployeeID = nextStepEmployeeID;
	}

	 

	public int getRoleID() {
		return RoleID;
	}

	public void setRoleID(int roleID) {
		RoleID = roleID;
	}


	public BPMStepAction(BPMStepConfig wBPMStepConfig) {
		super();

		this.BaseID = 0;
		this.CustomList = wBPMStepConfig.CustomList;
		this.FilePath = new ArrayList<String>();
		this.FlowType = wBPMStepConfig.FlowType;
		this.Remark = "";
		this.Result = BPMResultEnum.Undo.getValue();
		this.StepEnum = wBPMStepConfig.StepEnum;
		this.StepID = wBPMStepConfig.ID;
		this.StepName = wBPMStepConfig.StepName;
		this.StepType = wBPMStepConfig.StepType;
		this.RoleID=wBPMStepConfig.RoleID;

		// TODO Auto-generated constructor stub
	}
	 

	public int getBaseID() {
		return BaseID;
	}

	public void setBaseID(int baseID) {
		BaseID = baseID;
	}

	public int getFlowType() {
		return FlowType;
	}

	public void setFlowType(int flowType) {
		FlowType = flowType;
	}

	public int getStepEnum() {
		return StepEnum;
	}

	public void setStepEnum(int stepEnum) {
		StepEnum = stepEnum;
	}

	public int getStepType() {
		return StepType;
	}

	public void setStepType(int stepType) {
		StepType = stepType;
	}

	public int getResult() {
		return Result;
	}

	public void setResult(int result) {
		Result = result;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public List<String> getFilePath() {
		return FilePath;
	}

	public void setFilePath(List<String> filePath) {
		FilePath = filePath;
	}

	public List<BPMCustom> getCustomList() {
		return CustomList;
	}

	public void setCustomList(List<BPMCustom> customList) {
		CustomList = customList;
	}

	public int getSubmitorID() {
		return SubmitorID;
	}

	public void setSubmitorID(int submitorID) {
		SubmitorID = submitorID;
	}

	public String getSubmitorInfo() {
		return SubmitorInfo;
	}

	public void setSubmitorInfo(String submitorInfo) {
		SubmitorInfo = submitorInfo;
	}

	public int getStepID() {
		return StepID;
	}

	public void setStepID(int stepID) {
		StepID = stepID;
	}

	public String getStepName() {
		return StepName;
	}

	public void setStepName(String stepName) {
		StepName = stepName;
	}

	public Calendar getSubmitTime() {
		return SubmitTime;
	}

	public void setSubmitTime(Calendar submitTime) {
		SubmitTime = submitTime;
	}

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

	public Boolean getIsAllow() {
		return IsAllow;
	}

	public void setIsAllow(Boolean isAllow) {
		IsAllow = isAllow;
	}

}
