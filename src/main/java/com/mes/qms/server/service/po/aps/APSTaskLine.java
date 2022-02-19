package com.mes.qms.server.service.po.aps;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.mes.qms.server.service.mesenum.APSShiftPeriod;
import com.mes.qms.server.service.po.aps.APSMaterial;
import com.mes.qms.server.service.po.aps.APSMessage;
import com.mes.qms.server.service.po.aps.APSOrder;

public class APSTaskLine implements Serializable {
	private static final long serialVersionUID = 1L;

	public int ID = 0;

	public int OrderID = 0;
	/**
	 * 车号
	 */
	public String PartNo = "";

	public int WorkShopID = 0;

	public int LineID = 0;

	/**
	 * 标准工时(瓶颈工序)  即最大Part工时
	 */
	public int PartHours = 0;

	/**
	 * 换型时间
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
	 * 提交时刻
	 */
	public Calendar SubmitTime = Calendar.getInstance();

	/**
	 * 修改时刻
	 */
	public Calendar SessionTime = Calendar.getInstance();

	/**
	 * 工位任务列表
	 */
	public List<APSTaskPart> TaskPartList = new ArrayList<>();

	/**
	 * 物料需求列表
	 */
	public List<APSMaterial> MaterialList = new ArrayList<>(); // 物料需求

	/**
	 * 冲突消息列表
	 */
	public List<APSMessage> MessageList = new ArrayList<>(); // 消息

	/**
	 * 审核人
	 */
	public int AuditorID = 0;
	/**
	 * 审核时刻
	 */
	public Calendar AuditTime = Calendar.getInstance();

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
	 * 审核人名称
	 */
	public String Auditor;

	/**
	 * Bom编号
	 */
	public String BOMNo;

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
	public String TaskText;

	/**
	 * 修程工时
	 */
	public int WorkHour = 0; // 单工件工时

	/**
	 * 订单任务状态
	 */
	public int Status = 0;

	/**
	 * 计划开始时刻
	 */
	public Calendar StartTime = Calendar.getInstance();

	/**
	 * 计划结束时刻
	 */
	public Calendar EndTime = Calendar.getInstance();

	/**
	 * 计划性质（周/月）
	 */
	public int ShiftPeriod = APSShiftPeriod.Month.getValue();

	public APSTaskLine() {
		this.ID = 0;
		this.OrderID = 0;
		this.WorkShopID = 0;
		this.LineID = 0;

		this.PartHours = 0;
		this.CraftMinutes = 0;
		this.ShiftID = 0;
		this.PlanerID = 0;
		this.AuditorID = 0;
		this.Priority = 0;
		this.Active = 0;
		this.WorkHour = 0;
		this.Status = 0;

		this.OrderNo = "";
		this.ProductNo = "";

		this.MaterialNo = "";
		this.MaterialName = "";
		this.Auditor = "";
		this.BOMNo = "";
		this.PlanerName = "";
		this.WorkShopName = "";
		this.LineName = "";
		this.TaskText = "";

		this.SubmitTime = Calendar.getInstance();
		this.SessionTime = Calendar.getInstance();
		this.StartTime = Calendar.getInstance();
		this.EndTime = Calendar.getInstance();
		this.AuditTime = Calendar.getInstance();
		this.TaskPartList = new ArrayList<>();
		this.MaterialList = new ArrayList<>();
		this.MessageList = new ArrayList<>();
	}

	public APSTaskLine(int wCompanyID, APSOrder wOrder) {
		this.WorkShopID = wOrder.WorkShopID;
		this.LineID = wOrder.LineID;

		this.OrderID = wOrder.ID;
		this.OrderNo = wOrder.OrderNo;
		this.ProductNo = wOrder.ProductNo;
		this.MaterialNo = wOrder.MaterialNo;
		this.MaterialName = wOrder.MaterialName;
		this.BOMNo = wOrder.BOMNo;
		this.PlanerName = "";

		this.WorkHour = wOrder.WorkHour;
		this.SubmitTime = Calendar.getInstance();
		this.SessionTime = Calendar.getInstance();
		this.StartTime = wOrder.StartTime;
		this.EndTime = wOrder.EndTime;
		this.AuditTime = Calendar.getInstance();
		this.TaskPartList = new ArrayList<>();
		this.MaterialList = new ArrayList<>();
		this.MessageList = new ArrayList<>();

		this.TaskText = "";
	}

	public int getID() {
		return ID;
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

	public int getPartHours() {
		return PartHours;
	}

	public void setPartHours(int partHours) {
		PartHours = partHours;
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

	public Calendar getSubmitTime() {
		return SubmitTime;
	}

	public void setSubmitTime(Calendar submitTime) {
		SubmitTime = submitTime;
	}

	public Calendar getSessionTime() {
		return SessionTime;
	}

	public void setSessionTime(Calendar sessionTime) {
		SessionTime = sessionTime;
	}

	public List<APSTaskPart> getTaskPartList() {
		return TaskPartList;
	}

	public void setTaskPartList(List<APSTaskPart> taskPartList) {
		TaskPartList = taskPartList;
	}

	public List<APSMaterial> getMaterialList() {
		return MaterialList;
	}

	public void setMaterialList(List<APSMaterial> materialList) {
		MaterialList = materialList;
	}

	public List<APSMessage> getMessageList() {
		return MessageList;
	}

	public void setMessageList(List<APSMessage> messageList) {
		MessageList = messageList;
	}

	public int getAuditorID() {
		return AuditorID;
	}

	public void setAuditorID(int auditorID) {
		AuditorID = auditorID;
	}

	public Calendar getAuditTime() {
		return AuditTime;
	}

	public void setAuditTime(Calendar auditTime) {
		AuditTime = auditTime;
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

	public String getAuditor() {
		return Auditor;
	}

	public void setAuditor(String auditor) {
		Auditor = auditor;
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

	public int getWorkHour() {
		return WorkHour;
	}

	public void setWorkHour(int workHour) {
		WorkHour = workHour;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
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

	public String getPartNo() {
		return PartNo;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public int getShiftPeriod() {
		return ShiftPeriod;
	}

	public void setShiftPeriod(int shiftPeriod) {
		ShiftPeriod = shiftPeriod;
	}

}
