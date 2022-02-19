package com.mes.qms.server.service.po.aps;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class APSOrder implements Serializable {
	private static final long serialVersionUID = 1L;

	// [DataMember(Name = "ID", Order = 0)]
	public int ID = 0;
	// [DataMember(Name = "OrderNo", Order = 1)]
	public String OrderNo= "";
	// [DataMember(Name = "ProductNo", Order = 2)]
	public String ProductNo= "";
	// [DataMember(Name = "MaterialNo", Order = 3)]
	public String MaterialNo= "";
	// [DataMember(Name = "MaterialName", Order = 4)]
	public String MaterialName= "";
	// [DataMember(Name = "FQTY", Order = 5)]
	public int FQTY = 0; // 订单计划数量
	// [DataMember(Name = "WorkShopID", Order = 6)]
	public int WorkShopID = 0;
	// [DataMember(Name = "LineID", Order = 7)]
	public int LineID = 0;
	// [DataMember(Name = "PartID", Order = 8)]
	public int PartID = 0;
	// [DataMember(Name = "BOMNo", Order = 9)]
	public String BOMNo= "";
	// [DataMember(Name = "Priority", Order = 10)]
	public int Priority = 0;
	// [DataMember(Name = "Status", Order = 11)]
	public int Status = 0;
	// [DataMember(Name = "Type", Order = 12)]
	public short Type = 0;
	// [DataMember(Name = "StartTime", Order = 13)]
	public Calendar StartTime= Calendar.getInstance();
	// [DataMember(Name = "EndTime", Order = 14)]
	public Calendar EndTime= Calendar.getInstance();
	// [DataMember(Name = "CreatorID", Order = 15)]
	public int CreatorID = 0;
	// [DataMember(Name = "AuditorID", Order = 16)]
	public int AuditorID = 0;
	// [DataMember(Name = "Creator", Order = 17)]
	public String Creator= "";
	// [DataMember(Name = "Auditor", Order = 18)]
	public String Auditor= "";
	// [DataMember(Name = "CreateTime", Order = 19)]
	public Calendar CreateTime= Calendar.getInstance();
	// [DataMember(Name = "AuditTime", Order = 20)]
	public Calendar AuditTime= Calendar.getInstance();
	// [DataMember(Name = "Active", Order = 21)]
	public int Active = 0;
	// [DataMember(Name = "OrderMode", Order = 22)]
	public int OrderMode = 0;

	// 计算用途
	// [DataMember(Name = "Shifts", Order = 23)]
	public float Shifts= 0.0f;
	// [DataMember(Name = "FQTYShift", Order = 24)]
	public int FQTYShift= 0; // 计划班产
	// [DataMember(Name = "EPQ", Order = 25)]
	public int EPQ = 0;   // 暂时没用
	// [DataMember(Name = "FQTYGood", Order = 26)]
	public int FQTYGood = 0;
	// [DataMember(Name = "PartName", Order = 27)]
	public String PartName= "";
	// [DataMember(Name = "TaskName", Order = 28)]
	public String TaskName= "";
	// [DataMember(Name = "FQTYDone", Order = 29)]
	public int FQTYDone = 0; // 订单完工数量
	// [DataMember(Name = "WorkHour", Order = 30)]
	public int WorkHour = 0; // 工件标准工时
	// [DataMember(Name = "WorkHours", Order = 31)]
	public int WorkHours= 0; // 工件总工时
	// [DataMember(Name = "ShiftHours", Order = 32)]
	public int ShiftHours= 0; // 单班标准工时
	// [DataMember(Name = "StartMarginHours", Order = 33)]
	public int StartMarginHours= 0;// 首班可用工时
	// [DataMember(Name = "FQTYMargin", Order = 34)]
	public int FQTYMargin= 0; // 待执行数
	// [DataMember(Name = "FQTYPlaned", Order = 35)]
	public int FQTYPlaned= 0; // 未完成已计划数
	// [DataMember(Name = "MessageList", Order = 36)]
	public List<APSMessage> MessageList = new ArrayList<>(); // 异常消息

	public APSOrder() {
		this.ID=0;
		this.OrderNo = "";
		this.ProductNo = "";
		this.MaterialNo = "";
		this.MaterialName = "";
		this.FQTY=0;
		this.WorkShopID=0;
		this.LineID=0;
		this.PartID=0;
		this.BOMNo = "";
		this.Priority=0;
		this.Status=0;
		this.Type=0;
		this.StartTime= Calendar.getInstance();
		this.EndTime= Calendar.getInstance();
		this.CreatorID=0;
		this.AuditorID=0;
		this.Creator = "";
		this.Auditor = "";
		this.CreateTime= Calendar.getInstance();
		this.AuditTime= Calendar.getInstance();
		this.Active=0;
		this.OrderMode=0;
		this.Shifts=0.0f;
		this.FQTYShift=0;
		this.EPQ=0;
		this.FQTYGood=0;
		this.PartName = "";
		this.TaskName = "";
		this.FQTYDone=0;
		this.WorkHour=0;
		this.WorkHours=0;
		this.ShiftHours=0;
		this.StartMarginHours=0;
		this.FQTYMargin=0;
		this.FQTYPlaned=0;
		this.MessageList = new ArrayList<>();
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
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

	public int getFQTY() {
		return FQTY;
	}

	public void setFQTY(int fQTY) {
		FQTY = fQTY;
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

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public short getType() {
		return Type;
	}

	public void setType(short type) {
		Type = type;
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

	public int getCreatorID() {
		return CreatorID;
	}

	public void setCreatorID(int creatorID) {
		CreatorID = creatorID;
	}

	public int getAuditorID() {
		return AuditorID;
	}

	public void setAuditorID(int auditorID) {
		AuditorID = auditorID;
	}

	public String getCreator() {
		return Creator;
	}

	public void setCreator(String creator) {
		Creator = creator;
	}

	public String getAuditor() {
		return Auditor;
	}

	public void setAuditor(String auditor) {
		Auditor = auditor;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public Calendar getAuditTime() {
		return AuditTime;
	}

	public void setAuditTime(Calendar auditTime) {
		AuditTime = auditTime;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public int getOrderMode() {
		return OrderMode;
	}

	public void setOrderMode(int orderMode) {
		OrderMode = orderMode;
	}

	public float getShifts() {
		return Shifts;
	}

	public void setShifts(float shifts) {
		Shifts = shifts;
	}

	public int getFQTYShift() {
		return FQTYShift;
	}

	public void setFQTYShift(int fQTYShift) {
		FQTYShift = fQTYShift;
	}

	public int getEPQ() {
		return EPQ;
	}

	public void setEPQ(int ePQ) {
		EPQ = ePQ;
	}

	public int getFQTYGood() {
		return FQTYGood;
	}

	public void setFQTYGood(int fQTYGood) {
		FQTYGood = fQTYGood;
	}

	public String getPartName() {
		return PartName;
	}

	public void setPartName(String partName) {
		PartName = partName;
	}

	public String getTaskName() {
		return TaskName;
	}

	public void setTaskName(String taskName) {
		TaskName = taskName;
	}

	public int getFQTYDone() {
		return FQTYDone;
	}

	public void setFQTYDone(int fQTYDone) {
		FQTYDone = fQTYDone;
	}

	public int getWorkHour() {
		return WorkHour;
	}

	public void setWorkHour(int workHour) {
		WorkHour = workHour;
	}

	public int getWorkHours() {
		return WorkHours;
	}

	public void setWorkHours(int workHours) {
		WorkHours = workHours;
	}

	public int getShiftHours() {
		return ShiftHours;
	}

	public void setShiftHours(int shiftHours) {
		ShiftHours = shiftHours;
	}

	public int getStartMarginHours() {
		return StartMarginHours;
	}

	public void setStartMarginHours(int startMarginHours) {
		StartMarginHours = startMarginHours;
	}

	public int getFQTYMargin() {
		return FQTYMargin;
	}

	public void setFQTYMargin(int fQTYMargin) {
		FQTYMargin = fQTYMargin;
	}

	public int getFQTYPlaned() {
		return FQTYPlaned;
	}

	public void setFQTYPlaned(int fQTYPlaned) {
		FQTYPlaned = fQTYPlaned;
	}

	public List<APSMessage> getMessageList() {
		return MessageList;
	}

	public void setMessageList(List<APSMessage> messageList) {
		MessageList = messageList;
	}
	
}
