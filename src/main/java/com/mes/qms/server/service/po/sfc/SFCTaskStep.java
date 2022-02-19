package com.mes.qms.server.service.po.sfc;

import java.io.Serializable;
import java.util.Calendar;

public class SFCTaskStep implements Serializable {
	private static final long serialVersionUID = 1L;
	public int ID = 0;
	public int OrderID = 0;

	public String PartNo = "";
	public int TaskLineID = 0;
	public int TaskPartID = 0;

	public int TaskStepID = 0;
	public int WorkShopID = 0;
	public int LineID = 0;
	public int PartID = 0;
	public int StepID = 0;
	public int ProductID = 0;

	public int MonitorID = 0;
	public String Monitor = "";

	public int ShiftID = 0;

	public double WorkHour = 0.0D;

	public int OperatorID = 0;
	public String Operator = "";

	public Calendar CreateTime = Calendar.getInstance();

	public Calendar ReadyTime = Calendar.getInstance();

	public Calendar StartTime = Calendar.getInstance();

	public Calendar EndTime = Calendar.getInstance();

	public int IsStartWork = 0;

	public int Status = 0;
	public String OrderNo = "";
	public String ProductNo = "";
	public String PartName = "";
	public String MaterialNo = "";
	public String MaterialName = "";
	public int PlanerID = 0;
	public String PlanerName = "";
	public String PartPointName = "";
	public String LineName = "";
	public int StepOrderID = 0;
	public String TaskText = "";
	public Calendar ShiftDate = Calendar.getInstance();
	public int CustomerID = 0;
	public String CustomerName = "";

	public int Type = 0;

	public int DoneCount = 0;

	public String Operators = "";

	/**
	 * 编辑时间
	 */
	public Calendar EditTime = Calendar.getInstance();
	/**
	 * 实际工时
	 */
	public double RealHour = 0.0;

	public int OrderNum = 0;

	public int Active = 1;

	public SFCTaskStep() {
		this.ID = 0;
		this.OrderID = 0;
		this.TaskLineID = 0;
		this.TaskPartID = 0;
		this.LineID = 0;

		this.PartID = 0;

		this.ShiftID = 0;
		this.ProductID = 0;
		this.Status = 0;
		this.PlanerID = 0;

		this.StepOrderID = 0;

		this.OrderNo = "";
		this.ProductNo = "";
		this.LineName = "";
		this.PartName = "";
		this.PartPointName = "";

		this.MaterialNo = "";
		this.MaterialName = "";
		this.PlanerName = "";
		this.TaskText = "";

		this.StartTime = Calendar.getInstance();
		this.EndTime = Calendar.getInstance();
		this.ShiftDate = Calendar.getInstance();
	}

	public SFCTaskStep Clone() {
		SFCTaskStep wTaskStep = new SFCTaskStep();
		wTaskStep.ID = this.ID;
		wTaskStep.OrderID = this.OrderID;
		wTaskStep.TaskLineID = this.TaskLineID;
		wTaskStep.TaskPartID = this.TaskPartID;
		wTaskStep.LineID = this.LineID;
		wTaskStep.PartID = this.PartID;
		wTaskStep.ShiftID = this.ShiftID;
		wTaskStep.ProductID = this.ProductID;

		wTaskStep.StartTime = this.StartTime;
		wTaskStep.EndTime = this.EndTime;
		wTaskStep.Status = this.Status;
		wTaskStep.OrderNo = this.OrderNo;
		wTaskStep.ProductNo = this.ProductNo;

		wTaskStep.PartName = this.PartName;
		wTaskStep.MaterialNo = this.MaterialNo;
		wTaskStep.MaterialName = this.MaterialName;
		wTaskStep.PlanerID = this.PlanerID;
		wTaskStep.PlanerName = this.PlanerName;

		wTaskStep.LineName = this.LineName;
		wTaskStep.StepOrderID = this.StepOrderID;
		wTaskStep.TaskText = this.TaskText;
		wTaskStep.ShiftDate = this.ShiftDate;

		return wTaskStep;
	}

	public int getIsStartWork() {
		return this.IsStartWork;
	}

	public int getDoneCount() {
		return this.DoneCount;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public void setDoneCount(int doneCount) {
		this.DoneCount = doneCount;
	}

	public void setIsStartWork(int isStartWork) {
		this.IsStartWork = isStartWork;
	}

	public int getType() {
		return this.Type;
	}

	public String getOperators() {
		return this.Operators;
	}

	public void setOperators(String operators) {
		this.Operators = operators;
	}

	public void setType(int type) {
		this.Type = type;
	}

	public int getID() {
		return this.ID;
	}

	public void setID(int iD) {
		this.ID = iD;
	}

	public int getTaskStepID() {
		return this.TaskStepID;
	}

	public int getMonitorID() {
		return this.MonitorID;
	}

	public void setMonitorID(int monitorID) {
		this.MonitorID = monitorID;
	}

	public String getMonitor() {
		return this.Monitor;
	}

	public void setMonitor(String monitor) {
		this.Monitor = monitor;
	}

	public void setTaskStepID(int taskStepID) {
		this.TaskStepID = taskStepID;
	}

	public String getOperator() {
		return this.Operator;
	}

	public void setOperator(String operator) {
		this.Operator = operator;
	}

	public int getOrderID() {
		return this.OrderID;
	}

	public void setOrderID(int orderID) {
		this.OrderID = orderID;
	}

	public int getTaskLineID() {
		return this.TaskLineID;
	}

	public void setTaskLineID(int taskLineID) {
		this.TaskLineID = taskLineID;
	}

	public int getTaskPartID() {
		return this.TaskPartID;
	}

	public void setTaskPartID(int taskPartID) {
		this.TaskPartID = taskPartID;
	}

	public int getLineID() {
		return this.LineID;
	}

	public void setLineID(int lineID) {
		this.LineID = lineID;
	}

	public int getPartID() {
		return this.PartID;
	}

	public void setPartID(int partID) {
		this.PartID = partID;
	}

	public Calendar getStartTime() {
		return this.StartTime;
	}

	public void setStartTime(Calendar startTime) {
		this.StartTime = startTime;
	}

	public String getPartNo() {
		return this.PartNo;
	}

	public void setPartNo(String partNo) {
		this.PartNo = partNo;
	}

	public int getWorkShopID() {
		return this.WorkShopID;
	}

	public void setWorkShopID(int workShopID) {
		this.WorkShopID = workShopID;
	}

	public int getStepID() {
		return this.StepID;
	}

	public void setStepID(int stepID) {
		this.StepID = stepID;
	}

	public int getProductID() {
		return this.ProductID;
	}

	public void setProductID(int productID) {
		this.ProductID = productID;
	}

	public int getShiftID() {
		return this.ShiftID;
	}

	public void setShiftID(int shiftID) {
		this.ShiftID = shiftID;
	}

	public double getWorkHour() {
		return this.WorkHour;
	}

	public void setWorkHour(double workHour) {
		this.WorkHour = workHour;
	}

	public int getOperatorID() {
		return this.OperatorID;
	}

	public void setOperatorID(int operatorID) {
		this.OperatorID = operatorID;
	}

	public Calendar getCreateTime() {
		return this.CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		this.CreateTime = createTime;
	}

	public Calendar getReadyTime() {
		return this.ReadyTime;
	}

	public void setReadyTime(Calendar readyTime) {
		this.ReadyTime = readyTime;
	}

	public Calendar getEndTime() {
		return this.EndTime;
	}

	public void setEndTime(Calendar endTime) {
		this.EndTime = endTime;
	}

	public int getStatus() {
		return this.Status;
	}

	public void setStatus(int status) {
		this.Status = status;
	}

	public String getOrderNo() {
		return this.OrderNo;
	}

	public void setOrderNo(String orderNo) {
		this.OrderNo = orderNo;
	}

	public String getProductNo() {
		return this.ProductNo;
	}

	public void setProductNo(String productNo) {
		this.ProductNo = productNo;
	}

	public String getPartName() {
		return this.PartName;
	}

	public void setPartName(String partName) {
		this.PartName = partName;
	}

	public String getMaterialNo() {
		return this.MaterialNo;
	}

	public void setMaterialNo(String materialNo) {
		this.MaterialNo = materialNo;
	}

	public String getMaterialName() {
		return this.MaterialName;
	}

	public void setMaterialName(String materialName) {
		this.MaterialName = materialName;
	}

	public int getPlanerID() {
		return this.PlanerID;
	}

	public void setPlanerID(int planerID) {
		this.PlanerID = planerID;
	}

	public String getPlanerName() {
		return this.PlanerName;
	}

	public void setPlanerName(String planerName) {
		this.PlanerName = planerName;
	}

	public String getPartPointName() {
		return this.PartPointName;
	}

	public void setPartPointName(String partPointName) {
		this.PartPointName = partPointName;
	}

	public String getLineName() {
		return this.LineName;
	}

	public void setLineName(String lineName) {
		this.LineName = lineName;
	}

	public int getStepOrderID() {
		return this.StepOrderID;
	}

	public void setStepOrderID(int stepOrderID) {
		this.StepOrderID = stepOrderID;
	}

	public String getTaskText() {
		return this.TaskText;
	}

	public void setTaskText(String taskText) {
		this.TaskText = taskText;
	}

	public Calendar getShiftDate() {
		return this.ShiftDate;
	}

	public void setShiftDate(Calendar shiftDate) {
		this.ShiftDate = shiftDate;
	}

	public Calendar getEditTime() {
		return EditTime;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
	}

	public int getCustomerID() {
		return CustomerID;
	}

	public void setCustomerID(int customerID) {
		CustomerID = customerID;
	}

	public String getCustomerName() {
		return CustomerName;
	}

	public void setCustomerName(String customerName) {
		CustomerName = customerName;
	}

	public double getRealHour() {
		return RealHour;
	}

	public void setRealHour(double realHour) {
		RealHour = realHour;
	}

	public int getOrderNum() {
		return OrderNum;
	}

	public void setOrderNum(int orderNum) {
		OrderNum = orderNum;
	}
}

/*
 * Location: C:\Users\Shris\Desktop\新建文件夹
 * (5)\MESLOCOAPS.zip!\WEB-INF\classes\com\mes\loco\aps\server\service\po\sfc\
 * SFCTaskStep.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.2
 */