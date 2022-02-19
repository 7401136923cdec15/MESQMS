package com.mes.qms.server.service.po.bpm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mes.qms.server.service.mesenum.BPMStepTypes;

/**
 * 流程步骤配置表
 * 
 * @author ShrisJava
 *
 */
public class BPMStepConfig implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID;

	/**
	 * 流程配置ID
	 */
	public int FlowID;

	/**
	 * 流程枚举
	 */
	public int FlowType;

	/**
	 * 流程名称 （不存）
	 */
	public String FlowName;

	/**
	 * 步骤枚举 代表另外业务需要  现在可以不用
	 */
	public int StepEnum;

	/**
	 * 步骤名称
	 */
	public String StepName="";

	/**
	 * 步骤类型 跟存数据相关 【开始 过程 结束】
	 */
	public int StepType = BPMStepTypes.Default.getValue();

	/**
	 * 步骤顺序 暂不使用
	 */
	public int StepOrder;

	/**
	 * 下一步内容
	 */
	public Map<String, String> NextOrderMap = new HashMap<String, String>();

	/**
	 * 顺序类型 0默认 前置完成 1 前置单个完成 2可根据条件忽略 3
	 */
	public int OrderType;

	public int Active;
	
	/**
	 * 步骤权限
	 */
	public int RoleID;
	
	/**
	 * 步骤名称
	 */
	public String RoleName="";

	/**
	 * 步骤需填充字段
	 */
	public List<BPMCustom> CustomList = new ArrayList<>();

	public BPMStepConfig() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getFlowType() {
		return FlowType;
	}

	public void setFlowType(int flowType) {
		FlowType = flowType;
	}

	public String getFlowName() {
		return FlowName;
	}

	public void setFlowName(String flowName) {
		FlowName = flowName;
	}

	public int getStepEnum() {
		return StepEnum;
	}

	public void setStepEnum(int stepEnum) {
		StepEnum = stepEnum;
	}

	public String getStepName() {
		return StepName;
	}

	public void setStepName(String stepName) {
		StepName = stepName;
	}

	public int getStepType() {
		return StepType;
	}

	public void setStepType(int stepType) {
		StepType = stepType;
	}

	public int getStepOrder() {
		return StepOrder;
	}

	public void setStepOrder(int stepOrder) {
		StepOrder = stepOrder;
	}

	public int getOrderType() {
		return OrderType;
	}

	public void setOrderType(int orderType) {
		OrderType = orderType;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public List<BPMCustom> getCustomList() {
		return CustomList;
	}

	public void setCustomList(List<BPMCustom> customList) {
		CustomList = customList;
	}

	public Map<String, String> getNextOrderMap() {
		return NextOrderMap;
	}

	public void setNextOrderMap(Map<String, String> nextOrderMap) {
		NextOrderMap = nextOrderMap;
	}

	public int getFlowID() {
		return FlowID;
	}

	public void setFlowID(int flowID) {
		FlowID = flowID;
	}
}
