package com.mes.qms.server.service.po.sfc;

import java.io.Serializable;

/**
 * 车辆转序情况，第一层(车辆整体进度)
 */
public class SFCTrainProgress02 implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 订单ID
	 */
	public int OrderID = 0;
	/**
	 * 车号
	 */
	public String PartNo = "";
	/**
	 * 修程
	 */
	public String LineName = "";
	/**
	 * 局段
	 */
	public String Customer = "";
	/**
	 * 车型
	 */
	public String ProductNo = "";
	/**
	 * 工位ID
	 */
	public int PartID = 0;
	/**
	 * 工位名称
	 */
	public String PartName = "";
	/**
	 * 工位任务状态
	 */
	public int Status = 0;
	/**
	 * 转序状态 0未转序 1转序中 2已转序
	 */
	public int IsTurnOrder = 0;
	/**
	 * 转序提示
	 */
	public String TurnOrderTip = "";
	/**
	 * 工序总数
	 */
	public int StepNumber = 0;
	/**
	 * 工序完成数
	 */
	public int StepFinishedNumber = 0;
	/**
	 * 不合格评审总数
	 */
	public int NCRNumber = 0;
	/**
	 * 不合格评审完成数
	 */
	public int NCRFinishedNumber = 0;
	/**
	 * 返修总数
	 */
	public int RepairNumber = 0;
	/**
	 * 返修完成数
	 */
	public int RepairFinishedNumber = 0;

	public SFCTrainProgress02() {
	}

	public SFCTrainProgress02(int orderID, String partNo, String lineName, String customer, String productNo,
			int partID, String partName, int status, int isTurnOrder, String turnOrderTip, int stepNumber,
			int stepFinishedNumber, int nCRNumber, int nCRFinishedNumber, int repairNumber, int repairFinishedNumber) {
		super();
		OrderID = orderID;
		PartNo = partNo;
		LineName = lineName;
		Customer = customer;
		ProductNo = productNo;
		PartID = partID;
		PartName = partName;
		Status = status;
		IsTurnOrder = isTurnOrder;
		TurnOrderTip = turnOrderTip;
		StepNumber = stepNumber;
		StepFinishedNumber = stepFinishedNumber;
		NCRNumber = nCRNumber;
		NCRFinishedNumber = nCRFinishedNumber;
		RepairNumber = repairNumber;
		RepairFinishedNumber = repairFinishedNumber;
	}

	public int getNCRFinishedNumber() {
		return NCRFinishedNumber;
	}

	public int getRepairFinishedNumber() {
		return RepairFinishedNumber;
	}

	public void setNCRFinishedNumber(int nCRFinishedNumber) {
		NCRFinishedNumber = nCRFinishedNumber;
	}

	public void setRepairFinishedNumber(int repairFinishedNumber) {
		RepairFinishedNumber = repairFinishedNumber;
	}

	public int getOrderID() {
		return OrderID;
	}

	public String getPartNo() {
		return PartNo;
	}

	public int getPartID() {
		return PartID;
	}

	public String getPartName() {
		return PartName;
	}

	public int getStatus() {
		return Status;
	}

	public int getIsTurnOrder() {
		return IsTurnOrder;
	}

	public String getTurnOrderTip() {
		return TurnOrderTip;
	}

	public int getStepNumber() {
		return StepNumber;
	}

	public int getStepFinishedNumber() {
		return StepFinishedNumber;
	}

	public int getNCRNumber() {
		return NCRNumber;
	}

	public int getRepairNumber() {
		return RepairNumber;
	}

	public void setPartID(int partID) {
		PartID = partID;
	}

	public void setPartName(String partName) {
		PartName = partName;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public void setIsTurnOrder(int isTurnOrder) {
		IsTurnOrder = isTurnOrder;
	}

	public void setTurnOrderTip(String turnOrderTip) {
		TurnOrderTip = turnOrderTip;
	}

	public void setStepNumber(int stepNumber) {
		StepNumber = stepNumber;
	}

	public void setStepFinishedNumber(int stepFinishedNumber) {
		StepFinishedNumber = stepFinishedNumber;
	}

	public void setNCRNumber(int nCRNumber) {
		NCRNumber = nCRNumber;
	}

	public void setRepairNumber(int repairNumber) {
		RepairNumber = repairNumber;
	}

	public String getLineName() {
		return LineName;
	}

	public String getCustomer() {
		return Customer;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public void setCustomer(String customer) {
		Customer = customer;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}
}
