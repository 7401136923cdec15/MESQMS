package com.mes.qms.server.service.po.mss;

import java.io.Serializable;

import com.mes.qms.server.service.po.bpm.BPMTaskBase;

/**
 * 工艺标准bom变更异常评审
 */
public class MssBomChangeBPM extends BPMTaskBase implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 物料名称
	 */
	public String MaterialNames = "";
	/**
	 * 物料号
	 */
	public String MaterialNos = "";
	/**
	 * 订单ID
	 */
	public int OrderID = 0;
	/**
	 * 工位ID
	 */
	public int PartID = 0;
	/**
	 * 工序ID
	 */
	public int StepID = 0;
	/**
	 * 评审等级
	 */
	public int Level = 0;
	/**
	 * 评审结果
	 */
	public int Result = 0;
	/**
	 * 车号
	 */
	public String PartNo = "";
	/**
	 * 工位
	 */
	public String PartName = "";
	/**
	 * 工序
	 */
	public String StepName = "";

	public String getMaterialNames() {
		return MaterialNames;
	}

	public String getMaterialNos() {
		return MaterialNos;
	}

	public int getOrderID() {
		return OrderID;
	}

	public int getPartID() {
		return PartID;
	}

	public int getStepID() {
		return StepID;
	}

	public int getLevel() {
		return Level;
	}

	public int getResult() {
		return Result;
	}

	public String getPartNo() {
		return PartNo;
	}

	public String getPartName() {
		return PartName;
	}

	public String getStepName() {
		return StepName;
	}

	public void setMaterialNames(String materialNames) {
		MaterialNames = materialNames;
	}

	public void setMaterialNos(String materialNos) {
		MaterialNos = materialNos;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public void setPartID(int partID) {
		PartID = partID;
	}

	public void setStepID(int stepID) {
		StepID = stepID;
	}

	public void setLevel(int level) {
		Level = level;
	}

	public void setResult(int result) {
		Result = result;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public void setPartName(String partName) {
		PartName = partName;
	}

	public void setStepName(String stepName) {
		StepName = stepName;
	}
}
