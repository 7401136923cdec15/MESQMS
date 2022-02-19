package com.mes.qms.server.service.po.sfc;

import java.io.Serializable;

/**
 * 车辆转序情况，第三层(工序整体进度)
 */
public class SFCTrainProgress03 implements Serializable {
	/**
	 * 序列号
	 */
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
	 * 工序ID
	 */
	public int StepID = 0;
	/**
	 * 工序名称
	 */
	public String StepName = "";
	/**
	 * 工序任务ID
	 */
	public int TaskStepID = 0;
	/**
	 * 工序任务状态
	 */
	public int Status = 0;
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
	/**
	 * 自检单ID
	 */
	public int SelfTaskID = 0;
	/**
	 * 自检状态
	 */
	public int SelfStatus = 0;
	/**
	 * 互检单ID
	 */
	public int MutualTaskID = 0;
	/**
	 * 互检状态
	 */
	public int MutualStatus = 0;
	/**
	 * 专检单ID
	 */
	public int SpecialTaskID = 0;
	/**
	 * 专检状态
	 */
	public int SpecialStatus = 0;
	/**
	 * 预检单ID
	 */
	public int YJTaskID = 0;
	/**
	 * 预检状态
	 */
	public int YJStatus = 0;
	/**
	 * 判断该工序是否 拍照
	 */
	public int IsPic = 0;

	public SFCTrainProgress03() {
	}

	public int getNCRFinishedNumber() {
		return NCRFinishedNumber;
	}

	public SFCTrainProgress03(int orderID, String partNo, String lineName, String customer, String productNo,
			int partID, String partName, int stepID, String stepName, int taskStepID, int status, int nCRNumber,
			int nCRFinishedNumber, int repairNumber, int repairFinishedNumber, int selfTaskID, int selfStatus,
			int mutualTaskID, int mutualStatus, int specialTaskID, int specialStatus, int yJTaskID, int yJStatus) {
		super();
		OrderID = orderID;
		PartNo = partNo;
		LineName = lineName;
		Customer = customer;
		ProductNo = productNo;
		PartID = partID;
		PartName = partName;
		StepID = stepID;
		StepName = stepName;
		TaskStepID = taskStepID;
		Status = status;
		NCRNumber = nCRNumber;
		NCRFinishedNumber = nCRFinishedNumber;
		RepairNumber = repairNumber;
		RepairFinishedNumber = repairFinishedNumber;
		SelfTaskID = selfTaskID;
		SelfStatus = selfStatus;
		MutualTaskID = mutualTaskID;
		MutualStatus = mutualStatus;
		SpecialTaskID = specialTaskID;
		SpecialStatus = specialStatus;
		YJTaskID = yJTaskID;
		YJStatus = yJStatus;
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

	public int getStepID() {
		return StepID;
	}

	public String getStepName() {
		return StepName;
	}

	public int getTaskStepID() {
		return TaskStepID;
	}

	public int getSelfTaskID() {
		return SelfTaskID;
	}

	public int getSelfStatus() {
		return SelfStatus;
	}

	public int getMutualTaskID() {
		return MutualTaskID;
	}

	public int getMutualStatus() {
		return MutualStatus;
	}

	public int getSpecialTaskID() {
		return SpecialTaskID;
	}

	public int getSpecialStatus() {
		return SpecialStatus;
	}

	public void setStepID(int stepID) {
		StepID = stepID;
	}

	public void setStepName(String stepName) {
		StepName = stepName;
	}

	public void setTaskStepID(int taskStepID) {
		TaskStepID = taskStepID;
	}

	public void setSelfTaskID(int selfTaskID) {
		SelfTaskID = selfTaskID;
	}

	public void setSelfStatus(int selfStatus) {
		SelfStatus = selfStatus;
	}

	public void setMutualTaskID(int mutualTaskID) {
		MutualTaskID = mutualTaskID;
	}

	public void setMutualStatus(int mutualStatus) {
		MutualStatus = mutualStatus;
	}

	public void setSpecialTaskID(int specialTaskID) {
		SpecialTaskID = specialTaskID;
	}

	public void setSpecialStatus(int specialStatus) {
		SpecialStatus = specialStatus;
	}

	public int getYJTaskID() {
		return YJTaskID;
	}

	public int getYJStatus() {
		return YJStatus;
	}

	public void setYJTaskID(int yJTaskID) {
		YJTaskID = yJTaskID;
	}

	public void setYJStatus(int yJStatus) {
		YJStatus = yJStatus;
	}

	public int getIsPic() {
		return IsPic;
	}

	public void setIsPic(int isPic) {
		IsPic = isPic;
	}
}
