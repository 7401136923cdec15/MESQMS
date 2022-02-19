package com.mes.qms.server.service.po.aps;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 日计划 计划是唯一
 * 
 * @author ShrisJava
 *
 */
public class APSTaskStep implements Serializable {
	private static final long serialVersionUID = 1L;
	public int ID = 0;
	public int OrderID = 0; // 订单ID

	/**
	 * 车号
	 */
	public String PartNo = "";

	public int TaskLineID = 0;

	public int TaskPartID = 0;

	public int WorkShopID = 0;

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
	 * 车间名称
	 */
	public String WorkShopName = "";

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
	 * 物料号
	 */
	public String MaterialNo = "";
	/**
	 * 物料名称
	 */
	public String MaterialName = "";
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
	 * 标准工时
	 */
	public double WorkHour = 0;
	/**
	 * 已派工的人员
	 */
	public String Operators = "";
	/**
	 * 前端派工用(缓存)
	 */
	public List<Integer> OperatorList = new ArrayList<Integer>();
	/**
	 * 是否已派工
	 */
	public boolean IsDispatched = false;
	/**
	 * 互检人员列表
	 */
	public String MutualNames = "";
	/**
	 * 历史备注
	 */
	public List<APSTaskRemark> RemarkList = new ArrayList<APSTaskRemark>();
	/**
	 * 当前备注
	 */
	public APSTaskRemark Remark = new APSTaskRemark();
	/**
	 * 实际工时
	 */
	public double RealHour = 0.0;

	public Calendar PlanStartTime = Calendar.getInstance();

	public APSTaskStep() {
		this.ID = 0;
		this.OrderID = 0;
		this.TaskLineID = 0;
		this.TaskPartID = 0;
		this.LineID = 0;
		this.PartID = 0;
		this.StepID = 0;
		this.ShiftID = 0;
		this.PlanerID = 0;

		this.WorkHour = 0;
		this.Status = 0;

		this.OrderNo = "";
		this.ProductNo = "";

		this.MaterialNo = "";
		this.MaterialName = "";

		this.PlanerName = "";
		this.LineName = "";
		this.PartName = "";
		this.StepName = "";
		this.TaskText = "";

		Calendar wBaseTime = Calendar.getInstance();
		wBaseTime.set(2000, 0, 1);

		this.StartTime = wBaseTime;
		this.EndTime = wBaseTime;
		this.CreateTime = wBaseTime;
		this.ReadyTime = wBaseTime;
	}

	public int getID() {
		return ID;
	}

	public Calendar getPlanStartTime() {
		return PlanStartTime;
	}

	public void setPlanStartTime(Calendar planStartTime) {
		PlanStartTime = planStartTime;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getOrderID() {
		return OrderID;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public List<Integer> getOperatorList() {
		return OperatorList;
	}

	public void setOperatorList(List<Integer> operatorList) {
		OperatorList = operatorList;
	}

	public boolean isIsDispatched() {
		return IsDispatched;
	}

	public void setIsDispatched(boolean isDispatched) {
		IsDispatched = isDispatched;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public String getMutualNames() {
		return MutualNames;
	}

	public void setMutualNames(String mutualNames) {
		MutualNames = mutualNames;
	}

	public Calendar getReadyTime() {
		return ReadyTime;
	}

	public void setReadyTime(Calendar readyTime) {
		ReadyTime = readyTime;
	}

	public String getOperators() {
		return Operators;
	}

	public void setOperators(String operators) {
		Operators = operators;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public String getPartNo() {
		return PartNo;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public int getTaskLineID() {
		return TaskLineID;
	}

	public void setTaskLineID(int taskLineID) {
		TaskLineID = taskLineID;
	}

	public int getTaskPartID() {
		return TaskPartID;
	}

	public void setTaskPartID(int taskPartID) {
		TaskPartID = taskPartID;
	}

	public int getWorkShopID() {
		return WorkShopID;
	}

	public void setWorkShopID(int workShopID) {
		WorkShopID = workShopID;
	}

	public int getLineID() {
		return LineID;
	}

	public void setLineID(int lineID) {
		LineID = lineID;
	}

	public int getPartID() {
		return PartID;
	}

	public void setPartID(int partID) {
		PartID = partID;
	}

	public int getStepID() {
		return StepID;
	}

	public void setStepID(int stepID) {
		StepID = stepID;
	}

	public int getShiftID() {
		return ShiftID;
	}

	public void setShiftID(int shiftID) {
		ShiftID = shiftID;
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

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public String getOrderNo() {
		return OrderNo;
	}

	public void setOrderNo(String orderNo) {
		OrderNo = orderNo;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	public String getWorkShopName() {
		return WorkShopName;
	}

	public void setWorkShopName(String workShopName) {
		WorkShopName = workShopName;
	}

	public String getLineName() {
		return LineName;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public String getPartName() {
		return PartName;
	}

	public void setPartName(String partName) {
		PartName = partName;
	}

	public String getStepName() {
		return StepName;
	}

	public void setStepName(String stepName) {
		StepName = stepName;
	}

	public String getMaterialNo() {
		return MaterialNo;
	}

	public void setMaterialNo(String materialNo) {
		MaterialNo = materialNo;
	}

	public List<Integer> getOpeartorList() {
		return OperatorList;
	}

	public void setOpeartorList(List<Integer> opeartorList) {
		OperatorList = opeartorList;
	}

	public String getMaterialName() {
		return MaterialName;
	}

	public void setMaterialName(String materialName) {
		MaterialName = materialName;
	}

	public int getPlanerID() {
		return PlanerID;
	}

	public void setPlanerID(int planerID) {
		PlanerID = planerID;
	}

	public String getPlanerName() {
		return PlanerName;
	}

	public void setPlanerName(String planerName) {
		PlanerName = planerName;
	}

	public String getTaskText() {
		return TaskText;
	}

	public void setTaskText(String taskText) {
		TaskText = taskText;
	}

	public double getWorkHour() {
		return WorkHour;
	}

	public void setWorkHour(double workHour) {
		WorkHour = workHour;
	}

	public List<APSTaskRemark> getRemarkList() {
		return RemarkList;
	}

	public void setRemarkList(List<APSTaskRemark> remarkList) {
		RemarkList = remarkList;
	}

	public APSTaskRemark getRemark() {
		return Remark;
	}

	public void setRemark(APSTaskRemark remark) {
		Remark = remark;
	}

	public double getRealHour() {
		return RealHour;
	}

	public void setRealHour(double realHour) {
		RealHour = realHour;
	}
}
