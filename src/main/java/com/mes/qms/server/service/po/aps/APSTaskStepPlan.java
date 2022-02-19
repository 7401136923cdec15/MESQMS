package com.mes.qms.server.service.po.aps;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 工序计划排程结构
 * 
 * @author YouWang·Peng
 * @CreateTime 2021-7-19 09:40:42
 */
public class APSTaskStepPlan implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 订单ID
	 */
	public int OrderID = 0;
	/**
	 * 车号
	 */
	public String PartNo = "";
	/**
	 * 工位计划ID
	 */
	public int TaskPartID = 0;
	/**
	 * 修程ID
	 */
	public int LineID = 0;
	/**
	 * 工位ID
	 */
	public int PartID = 0;
	/**
	 * 工序ID
	 */
	public int StepID = 0;
	/**
	 * 创建时刻
	 */
	public Calendar CreateTime = Calendar.getInstance();
	/**
	 * 任务下达时刻
	 */
	public Calendar ReadyTime = Calendar.getInstance();
	/**
	 * 班次ID 用 Day生成
	 */
	public int ShiftID = 0; // 班次
	/**
	 * 任务开始时刻
	 */
	public Calendar StartTime = Calendar.getInstance();
	/**
	 * 任务结束时刻
	 */
	public Calendar EndTime = Calendar.getInstance();
	/**
	 * 订单任务状态
	 */
	public int Status = 0;
	/**
	 * 计划是否禁用状态
	 */
	public int Active = 0;
	/**
	 * 订单号
	 */
	public String OrderNo = "";
	/**
	 * 车型编码
	 */
	public String ProductNo = "";
	/**
	 * 修程名称
	 */
	public String LineName = "";
	/**
	 * 工位名称
	 */
	public String PartName = "";
	/**
	 * 工序名称
	 */
	public String StepName = "";
	/**
	 * 计划员
	 */
	public int PlanerID = 0;
	/**
	 * 计划员名称
	 */
	public String PlanerName = "";
	/**
	 * 任务备注
	 */
	public String TaskText = "";
	/**
	 * 当前备注
	 */
	public String Remark = "";

	public APSTaskStepPlan() {
		super();
	}

	public APSTaskStepPlan(int iD, int orderID, String partNo, int taskPartID, int lineID, int partID, int stepID,
			Calendar createTime, Calendar readyTime, int shiftID, Calendar startTime, Calendar endTime, int status,
			int active, String orderNo, String productNo, String lineName, String partName, String stepName,
			int planerID, String planerName, String taskText, String remark) {
		super();
		ID = iD;
		OrderID = orderID;
		PartNo = partNo;
		TaskPartID = taskPartID;
		LineID = lineID;
		PartID = partID;
		StepID = stepID;
		CreateTime = createTime;
		ReadyTime = readyTime;
		ShiftID = shiftID;
		StartTime = startTime;
		EndTime = endTime;
		Status = status;
		Active = active;
		OrderNo = orderNo;
		ProductNo = productNo;
		LineName = lineName;
		PartName = partName;
		StepName = stepName;
		PlanerID = planerID;
		PlanerName = planerName;
		TaskText = taskText;
		Remark = remark;
	}

	public int getID() {
		return ID;
	}

	public int getOrderID() {
		return OrderID;
	}

	public String getPartNo() {
		return PartNo;
	}

	public int getTaskPartID() {
		return TaskPartID;
	}

	public int getLineID() {
		return LineID;
	}

	public int getPartID() {
		return PartID;
	}

	public int getStepID() {
		return StepID;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public Calendar getReadyTime() {
		return ReadyTime;
	}

	public int getShiftID() {
		return ShiftID;
	}

	public Calendar getStartTime() {
		return StartTime;
	}

	public Calendar getEndTime() {
		return EndTime;
	}

	public int getStatus() {
		return Status;
	}

	public int getActive() {
		return Active;
	}

	public String getOrderNo() {
		return OrderNo;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public String getLineName() {
		return LineName;
	}

	public String getPartName() {
		return PartName;
	}

	public String getStepName() {
		return StepName;
	}

	public int getPlanerID() {
		return PlanerID;
	}

	public String getPlanerName() {
		return PlanerName;
	}

	public String getTaskText() {
		return TaskText;
	}

	public String getRemark() {
		return Remark;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public void setTaskPartID(int taskPartID) {
		TaskPartID = taskPartID;
	}

	public void setLineID(int lineID) {
		LineID = lineID;
	}

	public void setPartID(int partID) {
		PartID = partID;
	}

	public void setStepID(int stepID) {
		StepID = stepID;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public void setReadyTime(Calendar readyTime) {
		ReadyTime = readyTime;
	}

	public void setShiftID(int shiftID) {
		ShiftID = shiftID;
	}

	public void setStartTime(Calendar startTime) {
		StartTime = startTime;
	}

	public void setEndTime(Calendar endTime) {
		EndTime = endTime;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public void setActive(int active) {
		Active = active;
	}

	public void setOrderNo(String orderNo) {
		OrderNo = orderNo;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public void setPartName(String partName) {
		PartName = partName;
	}

	public void setStepName(String stepName) {
		StepName = stepName;
	}

	public void setPlanerID(int planerID) {
		PlanerID = planerID;
	}

	public void setPlanerName(String planerName) {
		PlanerName = planerName;
	}

	public void setTaskText(String taskText) {
		TaskText = taskText;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}
}
