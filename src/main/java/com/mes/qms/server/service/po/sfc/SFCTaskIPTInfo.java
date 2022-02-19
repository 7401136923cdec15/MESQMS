package com.mes.qms.server.service.po.sfc;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 检验单详情数据
 */
public class SFCTaskIPTInfo implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 行号
	 */
	public int ID = 0;
	/**
	 * 任务ID
	 */
	public int TaskID = 0;
	/**
	 * 车号
	 */
	public String PartNo = "";
	/**
	 * 修程
	 */
	public String LineName = "";
	/**
	 * 车型
	 */
	public String ProductNo = "";
	/**
	 * 局段
	 */
	public String CustomerName = "";
	/**
	 * 工位
	 */
	public String StationName = "";
	/**
	 * 工序
	 */
	public String StepName = "";
	/**
	 * 任务类型ID
	 */
	public int Type = 0;
	/**
	 * 任务类型
	 */
	public String TypeText = "";
	/**
	 * 相关人员
	 */
	public String Persons = "";
	/**
	 * 状态文本
	 */
	public String StatusText = "";
	/**
	 * 开始时刻
	 */
	public Calendar StartTime = Calendar.getInstance();
	/**
	 * 结束时刻
	 */
	public Calendar EndTime = Calendar.getInstance();
	/**
	 * 备注
	 */
	public String Remark = "";
	/**
	 * 总数
	 */
	public int TotalSize = 0;
	/**
	 * 完成数
	 */
	public int FinishSize = 0;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getTaskID() {
		return TaskID;
	}

	public void setTaskID(int taskID) {
		TaskID = taskID;
	}

	public String getPartNo() {
		return PartNo;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public String getLineName() {
		return LineName;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	public String getCustomerName() {
		return CustomerName;
	}

	public void setCustomerName(String customerName) {
		CustomerName = customerName;
	}

	public String getStationName() {
		return StationName;
	}

	public void setStationName(String stationName) {
		StationName = stationName;
	}

	public String getStepName() {
		return StepName;
	}

	public void setStepName(String stepName) {
		StepName = stepName;
	}

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public String getTypeText() {
		return TypeText;
	}

	public void setTypeText(String typeText) {
		TypeText = typeText;
	}

	public String getPersons() {
		return Persons;
	}

	public void setPersons(String persons) {
		Persons = persons;
	}

	public String getStatusText() {
		return StatusText;
	}

	public void setStatusText(String statusText) {
		StatusText = statusText;
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

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public int getTotalSize() {
		return TotalSize;
	}

	public void setTotalSize(int totalSize) {
		TotalSize = totalSize;
	}

	public int getFinishSize() {
		return FinishSize;
	}

	public void setFinishSize(int finishSize) {
		FinishSize = finishSize;
	}
}
