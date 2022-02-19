package com.mes.qms.server.service.po.aps;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.mes.qms.server.service.mesenum.APSShiftPeriod;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.oms.OMSOrder;

public class APSTaskPart implements Serializable {
	private static final long serialVersionUID = 1L;
	public int ID = 0;
	public int OrderID = 0; // 订单ID

	/**
	 * 车号
	 */
	public String PartNo = "";

	public int TaskLineID = 0;

	public int WorkShopID = 0;

	public int LineID = 0;

	public int PartID = 0;

	/**
	 * 标准工时
	 */
	public Double WorkHour = 0.0;

	/**
	 * 工艺时间
	 */
	public int CraftMinutes = 0;

	/**
	 * 班次ID
	 */
	public int ShiftID = 0;
	/**
	 * 计划员
	 */
	public int PlanerID = 0;

	/**
	 * 计划性质（周/月）
	 */
	public int ShiftPeriod = APSShiftPeriod.Month.getValue();

	/**
	 * 提交时刻
	 */
	public Calendar SubmitTime = Calendar.getInstance();

	/**
	 * 计划开始时刻
	 */
	public Calendar StartTime = Calendar.getInstance();

	/**
	 * 计划结束时刻
	 */
	public Calendar EndTime = Calendar.getInstance();

	/**
	 * 工序任务列表 任务开始后可获得
	 */
	public List<APSTaskStep> TaskStepList = new ArrayList<>();
	/**
	 * 唯一ID(用于前端显示甘特图)
	 */
	public int UniqueID;
	/**
	 * 树形结构(用户前端显示甘特图)
	 */
	public List<APSTaskPart> TaskPartList = new ArrayList<APSTaskPart>();
	/**
	 * 工位顺序(用户前端显示甘特图)
	 */
	public int PartOrder;
	/**
	 * 冲突消息(用于前端甘特图显示)
	 */
	public String APSMessage = "";
	/**
	 * 订单号
	 */
	public String OrderNo = "";
	/**
	 * 车型编码
	 */
	public String ProductNo = "";

	/**
	 * 物料号
	 */
	public String MaterialNo = "";
	/**
	 * 物料名称
	 */
	public String MaterialName = "";
	/**
	 * 工位名称
	 */
	public String PartName = "";
	/**
	 * Bom编号
	 */
	public String BOMNo = "";

	/**
	 * 订单优先级
	 */
	public int Priority = 0;
	/**
	 * 计划员名称
	 */
	public String PlanerName = "";

	/**
	 * 车间名称
	 */
	public String WorkShopName = "";

	/**
	 * 修程名称
	 */
	public String LineName = "";
	/**
	 * 计划是否禁用状态
	 */
	public int Active = 0; // 状态
	/**
	 * 任务备注
	 */
	public String TaskText = "";

	/**
	 * 延误时间
	 */
	public Double DelayHours = 0.0;

	/**
	 * 订单任务状态
	 */
	public int Status = 0;

	/**
	 * 班次日期(月计划：月的第一天，周计划：周的第一天)
	 */
	public Calendar ShiftDate = Calendar.getInstance();

	/**
	 * 状态文本
	 */
	public String StatusText = "";

	// 辅助信息
	/**
	 * 局段ID
	 */
	public int CustomerID = 0;
	/**
	 * 局段名称
	 */
	public String CustomerName = "";
	/**
	 * WBS编号
	 */
	public String WBSNO = "";
	/**
	 * 责任班组ID
	 */
	public int ClassID = 0;
	/**
	 * 责任班组名称
	 */
	public String ClassName = "";
	/**
	 * 开工时间
	 */
	public Calendar StartWorkTime = Calendar.getInstance();
	/**
	 * 完工时间
	 */
	public Calendar FinishWorkTime = Calendar.getInstance();
	/**
	 * 路线ID
	 */
	public int RouteID = 0;

	public int AuditID = 0;
	public String Auditor = "";

	public APSTaskPart() {
		this.ID = 0;
		this.OrderID = 0;
		this.TaskLineID = 0;
		this.LineID = 0;
		this.ShiftID = 0;
		this.PlanerID = 0;

		this.Priority = 0;
		this.Active = 0;
		this.WorkHour = 0.0;
		this.Status = 0;

		this.OrderNo = "";
		this.ProductNo = "";

		this.MaterialNo = "";
		this.MaterialName = "";
		this.BOMNo = "";
		this.PlanerName = "";
		this.WorkShopName = "";
		this.LineName = "";
		this.TaskText = "";

		this.SubmitTime = Calendar.getInstance();
		this.StartTime = Calendar.getInstance();
		this.EndTime = Calendar.getInstance();
		this.TaskStepList = new ArrayList<>();

		this.StartWorkTime.set(2000, 0, 1);
		this.FinishWorkTime.set(2000, 0, 1);
	}

	public APSTaskPart(BMSEmployee wLoginUser, APSManuCapacity wAPSManuCapacity, OMSOrder wOrder, int wShiftPeriod) {

		this.LineID = wOrder.LineID;
		this.PartID = wAPSManuCapacity.PartID;
		this.PartNo = wOrder.PartNo;
		this.OrderID = wOrder.ID;
		this.OrderNo = wOrder.OrderNo;
		this.ProductNo = wOrder.ProductNo;
		this.PartName = wAPSManuCapacity.PartName;
		this.BOMNo = wOrder.BOMNo;
		this.PlanerID = wLoginUser.ID;
		this.PlanerName = wLoginUser.Name;
		this.ShiftPeriod = wShiftPeriod;
		this.StartTime = Calendar.getInstance();
		this.TaskStepList = new ArrayList<>();
		this.SubmitTime = Calendar.getInstance();
		this.StartTime = Calendar.getInstance();
		this.TaskText = "";

		this.StartWorkTime.set(2000, 0, 1);
		this.FinishWorkTime.set(2000, 0, 1);
	}

	public APSTaskPart Clone() {
		APSTaskPart wTaskPart = new APSTaskPart();
		wTaskPart.LineID = this.LineID;
		wTaskPart.PartID = this.PartID;
		wTaskPart.OrderID = this.OrderID;
		wTaskPart.SubmitTime = this.SubmitTime;
		wTaskPart.OrderNo = this.OrderNo;
		wTaskPart.ProductNo = this.ProductNo;
		wTaskPart.MaterialNo = this.MaterialNo;
		wTaskPart.MaterialName = this.MaterialName;
		wTaskPart.PartName = this.PartName;
		wTaskPart.BOMNo = this.BOMNo;
		wTaskPart.PlanerName = this.PlanerName;
		wTaskPart.StartTime = this.StartTime;
		wTaskPart.EndTime = this.EndTime;
		wTaskPart.TaskStepList = new ArrayList<APSTaskStep>(this.TaskStepList);
		wTaskPart.TaskText = this.TaskText;
		wTaskPart.WorkHour = this.WorkHour;
		return wTaskPart;
	}

	public int getID() {
		return ID;
	}

	public Calendar getShiftDate() {
		return ShiftDate;
	}

	public int getPartOrder() {
		return PartOrder;
	}

	public int getAuditID() {
		return AuditID;
	}

	public void setAuditID(int auditID) {
		AuditID = auditID;
	}

	public String getAuditor() {
		return Auditor;
	}

	public void setAuditor(String auditor) {
		Auditor = auditor;
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

	public String getWBSNO() {
		return WBSNO;
	}

	public void setWBSNO(String wBSNO) {
		WBSNO = wBSNO;
	}

	public int getClassID() {
		return ClassID;
	}

	public void setClassID(int classID) {
		ClassID = classID;
	}

	public String getClassName() {
		return ClassName;
	}

	public void setClassName(String className) {
		ClassName = className;
	}

	public Calendar getStartWorkTime() {
		return StartWorkTime;
	}

	public void setStartWorkTime(Calendar startWorkTime) {
		StartWorkTime = startWorkTime;
	}

	public Calendar getFinishWorkTime() {
		return FinishWorkTime;
	}

	public void setFinishWorkTime(Calendar finishWorkTime) {
		FinishWorkTime = finishWorkTime;
	}

	public void setPartOrder(int partOrder) {
		PartOrder = partOrder;
	}

	public int getUniqueID() {
		return UniqueID;
	}

	public void setUniqueID(int uniqueID) {
		UniqueID = uniqueID;
	}

	public List<APSTaskPart> getTaskPartList() {
		return TaskPartList;
	}

	public void setTaskPartList(List<APSTaskPart> taskPartList) {
		TaskPartList = taskPartList;
	}

	public void setShiftDate(Calendar shiftDate) {
		ShiftDate = shiftDate;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getOrderID() {
		return OrderID;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public int getTaskLineID() {
		return TaskLineID;
	}

	public void setTaskLineID(int taskLineID) {
		TaskLineID = taskLineID;
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

	public String getPartNo() {
		return PartNo;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public int getWorkShopID() {
		return WorkShopID;
	}

	public void setWorkShopID(int workShopID) {
		WorkShopID = workShopID;
	}

	public Double getWorkHour() {
		return WorkHour;
	}

	public void setWorkHour(Double workHour) {
		WorkHour = workHour;
	}

	public int getCraftMinutes() {
		return CraftMinutes;
	}

	public void setCraftMinutes(int craftMinutes) {
		CraftMinutes = craftMinutes;
	}

	public int getShiftID() {
		return ShiftID;
	}

	public void setShiftID(int shiftID) {
		ShiftID = shiftID;
	}

	public int getPlanerID() {
		return PlanerID;
	}

	public void setPlanerID(int planerID) {
		PlanerID = planerID;
	}

	public int getShiftPeriod() {
		return ShiftPeriod;
	}

	public void setShiftPeriod(int shiftPeriod) {
		ShiftPeriod = shiftPeriod;
	}

	public Calendar getSubmitTime() {
		return SubmitTime;
	}

	public void setSubmitTime(Calendar submitTime) {
		SubmitTime = submitTime;
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

	public List<APSTaskStep> getTaskStepList() {
		return TaskStepList;
	}

	public void setTaskStepList(List<APSTaskStep> taskStepList) {
		TaskStepList = taskStepList;
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

	public String getMaterialNo() {
		return MaterialNo;
	}

	public void setMaterialNo(String materialNo) {
		MaterialNo = materialNo;
	}

	public String getMaterialName() {
		return MaterialName;
	}

	public void setMaterialName(String materialName) {
		MaterialName = materialName;
	}

	public String getPartName() {
		return PartName;
	}

	public void setPartName(String partName) {
		PartName = partName;
	}

	public String getBOMNo() {
		return BOMNo;
	}

	public void setBOMNo(String bOMNo) {
		BOMNo = bOMNo;
	}

	public int getPriority() {
		return Priority;
	}

	public void setPriority(int priority) {
		Priority = priority;
	}

	public String getPlanerName() {
		return PlanerName;
	}

	public void setPlanerName(String planerName) {
		PlanerName = planerName;
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

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public String getTaskText() {
		return TaskText;
	}

	public void setTaskText(String taskText) {
		TaskText = taskText;
	}

	public Double getDelayHours() {
		return DelayHours;
	}

	public void setDelayHours(Double delayHours) {
		DelayHours = delayHours;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public String getAPSMessage() {
		return APSMessage;
	}

	public void setAPSMessage(String aPSMessage) {
		APSMessage = aPSMessage;
	}

	public String getStatusText() {
		return StatusText;
	}

	public void setStatusText(String statusText) {
		StatusText = statusText;
	}

	public int getRouteID() {
		return RouteID;
	}

	public void setRouteID(int routeID) {
		RouteID = routeID;
	}
}
