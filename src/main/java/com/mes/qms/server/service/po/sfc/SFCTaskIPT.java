package com.mes.qms.server.service.po.sfc;

import com.mes.qms.server.service.mesenum.SFCTaskType;
import com.mes.qms.server.service.po.fmc.FMCStation;
import com.mes.qms.server.service.po.sfc.SFCIPTItem;
import com.mes.qms.server.service.po.sfc.SFCTaskIPT;
import com.mes.qms.server.shristool.LoggerTool;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 检验单
 */
public class SFCTaskIPT implements Serializable {
	private static final long serialVersionUID = 1L;
	public int ID = 0;

	public int LineID = 0;

	public int PartID = 0;

	public int PartPointID = 0;

	public int StationID = 0;

	public int ProductID = 0;

	public int TaskStepID = 0;

	public int TaskType = 0;

	public int ModuleVersionID = 0;

	public int OperatorID = 0;

	public int ShiftID = 0;

	public Calendar ActiveTime = Calendar.getInstance();

	public int Status = 0;

	public Calendar SubmitTime = Calendar.getInstance();

	public int Result = 0;

	public int WorkShopID = 0;

	public int TaskMode = 0;

	public int Times = 0;

	public int FQTYGood = 0;

	public int FQTYBad = 0;

	public String OperatorName;

	public String WorkShopName;

	public String LineName;

	public String PartName;

	public String PartPointName;

	public String StationName;

	public String OrderNo;

	public String ProductNo;

	public String StatusText;

	public String TypeText;

	public String ModeText;

	public int EventID = 0;

	public String PartNo = "";
	public int OrderID = 0;
	public List<SFCIPTItem> ItemList = new ArrayList<>();

	public int CustomerID = 0;

	public String CustomerName = "";

	public Calendar OperateTime = Calendar.getInstance();

	public int Type = 0;

	public Calendar StartTime = Calendar.getInstance();

	public Calendar EndTime = Calendar.getInstance();

	public List<Integer> OperatorList = new ArrayList<>();
	/**
	 * 操作人集合
	 */
	public String OperatorListNames = "";

	/**
	 * 段改标准
	 */
	public int PeriodChangeStandard = 0;

	// 辅助属性
	/**
	 * 互检人
	 */
	public String Mutualer = "";
	/**
	 * 自检员
	 */
	public String Selfer = "";

	public int Active = 1;

	/**
	 * 是否填写照片
	 */
	public int IsPic = 0;
	/**
	 * 图片相对地址
	 */
	public String PicUri = "";
	/**
	 * 备注信息
	 */
	public String Remark = "";

	/**
	 * 之前填写的图片
	 */
	public String OldPicUri = "";
	/**
	 * 之前填写的备注
	 */
	public String OldRemark = "";

	/**
	 * 检查员
	 */
	public String CheckerList = "";

	/**
	 * 责任人员
	 */
	public String DutyPerson = "";

	public SFCTaskIPT() {
		this.ID = 0;
		this.LineID = 0;
		this.PartID = 0;
		this.PartPointID = 0;
		this.StationID = 0;

		this.ProductID = 0;
		this.TaskStepID = 0;
		this.TaskType = 0;

		this.ModuleVersionID = 0;
		this.OperatorID = 0;
		this.ShiftID = 0;
		this.TaskMode = 0;
		this.Times = 0;

		this.FQTYGood = 0;
		this.FQTYBad = 0;
		this.WorkShopID = 0;
		this.EventID = 0;

		this.Status = 0;

		this.OperatorName = "";
		this.WorkShopName = "";
		this.LineName = "";
		this.PartName = "";
		this.PartPointName = "";
		this.StationName = "";
		this.OrderNo = "";

		this.ProductNo = "";
		this.StatusText = "";
		this.TypeText = "";
		this.ModeText = "";
		this.ActiveTime = Calendar.getInstance();
		this.SubmitTime = Calendar.getInstance();
		this.ItemList = new ArrayList<>();

		this.OperateTime.set(2000, 0, 1);
		this.StartTime.set(2000, 0, 1);
		this.EndTime.set(2000, 0, 1);
	}

	public SFCTaskIPT(FMCStation wStation, SFCTaskType wTaskYype) {
		this.StationID = wStation.ID;
		this.TaskType = wTaskYype.getValue();
		this.Status = 0;
		this.ActiveTime = Calendar.getInstance();
		this.SubmitTime = Calendar.getInstance();
		this.ModuleVersionID = wStation.IPTModuleID;
		this.WorkShopID = wStation.WorkShopID;
		this.LineID = wStation.LineID;
		this.ItemList = new ArrayList<>();
	}

	public SFCTaskIPT Clone() {
		SFCTaskIPT wItem = new SFCTaskIPT();
		try {
			wItem.LineID = this.LineID;
			wItem.PartID = this.PartID;
			wItem.PartPointID = this.PartPointID;
			wItem.StationID = this.StationID;
			wItem.ProductID = this.ProductID;
			wItem.TaskStepID = this.TaskStepID;
			wItem.TaskType = this.TaskType;
			wItem.ModuleVersionID = this.ModuleVersionID;
			wItem.OperatorID = this.OperatorID;
			wItem.ShiftID = this.ShiftID;
			wItem.ActiveTime = this.ActiveTime;
			wItem.Status = this.Status;
			wItem.SubmitTime = this.SubmitTime;
			wItem.Result = this.Result;
			wItem.WorkShopID = this.WorkShopID;
			wItem.TaskMode = this.TaskMode;
			wItem.Times = this.Times;
			wItem.FQTYGood = this.FQTYGood;
			wItem.FQTYBad = this.FQTYBad;
			wItem.EventID = this.EventID;
			wItem.OrderID = this.OrderID;
			wItem.OrderNo = this.OrderNo;
			wItem.PartNo = this.PartNo;
			wItem.Type = this.Type;
			wItem.ItemList = new ArrayList<>(this.ItemList);
		} catch (Exception ex) {
			LoggerTool.SaveException("SFCService", "SFCTaskIPT Clone", "Function Exception:" + ex.toString());
		}
		return wItem;
	}

	public int getID() {
		return this.ID;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public void setID(int iD) {
		this.ID = iD;
	}

	public int getIsPic() {
		return IsPic;
	}

	public String getPicUri() {
		return PicUri;
	}

	public String getRemark() {
		return Remark;
	}

	public void setIsPic(int isPic) {
		IsPic = isPic;
	}

	public void setPicUri(String picUri) {
		PicUri = picUri;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public int getLineID() {
		return this.LineID;
	}

	public int getCustomerID() {
		return this.CustomerID;
	}

	public List<Integer> getOperatorList() {
		return this.OperatorList;
	}

	public void setOperatorList(List<Integer> operatorList) {
		this.OperatorList = operatorList;
	}

	public void setCustomerID(int customerID) {
		this.CustomerID = customerID;
	}

	public String getCustomerName() {
		return this.CustomerName;
	}

	public void setCustomerName(String customerName) {
		this.CustomerName = customerName;
	}

	public int getResult() {
		return this.Result;
	}

	public Calendar getOperateTime() {
		return this.OperateTime;
	}

	public void setOperateTime(Calendar operateTime) {
		this.OperateTime = operateTime;
	}

	public int getType() {
		return this.Type;
	}

	public void setType(int type) {
		this.Type = type;
	}

	public Calendar getStartTime() {
		return this.StartTime;
	}

	public void setStartTime(Calendar startTime) {
		this.StartTime = startTime;
	}

	public Calendar getEndTime() {
		return this.EndTime;
	}

	public void setEndTime(Calendar endTime) {
		this.EndTime = endTime;
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

	public int getOrderID() {
		return this.OrderID;
	}

	public void setOrderID(int orderID) {
		this.OrderID = orderID;
	}

	public int getPartPointID() {
		return this.PartPointID;
	}

	public void setPartPointID(int partPointID) {
		this.PartPointID = partPointID;
	}

	public int getStationID() {
		return this.StationID;
	}

	public void setStationID(int stationID) {
		this.StationID = stationID;
	}

	public int getProductID() {
		return this.ProductID;
	}

	public void setProductID(int productID) {
		this.ProductID = productID;
	}

	public int getTaskStepID() {
		return this.TaskStepID;
	}

	public void setTaskStepID(int taskStepID) {
		this.TaskStepID = taskStepID;
	}

	public int getTaskType() {
		return this.TaskType;
	}

	public void setTaskType(int taskType) {
		this.TaskType = taskType;
	}

	public int getModuleVersionID() {
		return this.ModuleVersionID;
	}

	public void setModuleVersionID(int moduleVersionID) {
		this.ModuleVersionID = moduleVersionID;
	}

	public int getOperatorID() {
		return this.OperatorID;
	}

	public void setOperatorID(int operatorID) {
		this.OperatorID = operatorID;
	}

	public int getShiftID() {
		return this.ShiftID;
	}

	public void setShiftID(int shiftID) {
		this.ShiftID = shiftID;
	}

	public Calendar getActiveTime() {
		return this.ActiveTime;
	}

	public void setActiveTime(Calendar activeTime) {
		this.ActiveTime = activeTime;
	}

	public int getStatus() {
		return this.Status;
	}

	public void setStatus(int status) {
		this.Status = status;
	}

	public Calendar getSubmitTime() {
		return this.SubmitTime;
	}

	public void setSubmitTime(Calendar submitTime) {
		this.SubmitTime = submitTime;
	}

	public int isResult() {
		return this.Result;
	}

	public void setResult(int result) {
		this.Result = result;
	}

	public int getWorkShopID() {
		return this.WorkShopID;
	}

	public void setWorkShopID(int workShopID) {
		this.WorkShopID = workShopID;
	}

	public int getTaskMode() {
		return this.TaskMode;
	}

	public void setTaskMode(int taskMode) {
		this.TaskMode = taskMode;
	}

	public int getTimes() {
		return this.Times;
	}

	public void setTimes(int times) {
		this.Times = times;
	}

	public int getFQTYGood() {
		return this.FQTYGood;
	}

	public void setFQTYGood(int fQTYGood) {
		this.FQTYGood = fQTYGood;
	}

	public int getFQTYBad() {
		return this.FQTYBad;
	}

	public void setFQTYBad(int fQTYBad) {
		this.FQTYBad = fQTYBad;
	}

	public String getOperatorName() {
		return this.OperatorName;
	}

	public void setOperatorName(String operatorName) {
		this.OperatorName = operatorName;
	}

	public String getWorkShopName() {
		return this.WorkShopName;
	}

	public void setWorkShopName(String workShopName) {
		this.WorkShopName = workShopName;
	}

	public String getLineName() {
		return this.LineName;
	}

	public void setLineName(String lineName) {
		this.LineName = lineName;
	}

	public String getPartName() {
		return this.PartName;
	}

	public void setPartName(String partName) {
		this.PartName = partName;
	}

	public String getPartPointName() {
		return this.PartPointName;
	}

	public void setPartPointName(String partPointName) {
		this.PartPointName = partPointName;
	}

	public String getStationName() {
		return this.StationName;
	}

	public void setStationName(String stationName) {
		this.StationName = stationName;
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

	public String getStatusText() {
		return this.StatusText;
	}

	public void setStatusText(String statusText) {
		this.StatusText = statusText;
	}

	public String getTypeText() {
		return this.TypeText;
	}

	public void setTypeText(String typeText) {
		this.TypeText = typeText;
	}

	public String getModeText() {
		return this.ModeText;
	}

	public void setModeText(String modeText) {
		this.ModeText = modeText;
	}

	public int getEventID() {
		return this.EventID;
	}

	public void setEventID(int eventID) {
		this.EventID = eventID;
	}

	public List<SFCIPTItem> getItemList() {
		return this.ItemList;
	}

	public void setItemList(List<SFCIPTItem> itemList) {
		this.ItemList = itemList;
	}

	public String getPartNo() {
		return this.PartNo;
	}

	public void setPartNo(String partNo) {
		this.PartNo = partNo;
	}

	public int getPeriodChangeStandard() {
		return PeriodChangeStandard;
	}

	public void setPeriodChangeStandard(int periodChangeStandard) {
		PeriodChangeStandard = periodChangeStandard;
	}

	public String getMutualer() {
		return Mutualer;
	}

	public void setMutualer(String mutualer) {
		Mutualer = mutualer;
	}

	public String getOldPicUri() {
		return OldPicUri;
	}

	public String getOldRemark() {
		return OldRemark;
	}

	public void setOldPicUri(String oldPicUri) {
		OldPicUri = oldPicUri;
	}

	public void setOldRemark(String oldRemark) {
		OldRemark = oldRemark;
	}

	public String getOperatorListNames() {
		return OperatorListNames;
	}

	public void setOperatorListNames(String operatorListNames) {
		OperatorListNames = operatorListNames;
	}

	public String getCheckerList() {
		return CheckerList;
	}

	public void setCheckerList(String checkerList) {
		CheckerList = checkerList;
	}

	public String getSelfer() {
		return Selfer;
	}

	public void setSelfer(String selfer) {
		Selfer = selfer;
	}

	public String getDutyPerson() {
		return DutyPerson;
	}

	public void setDutyPerson(String dutyPerson) {
		DutyPerson = dutyPerson;
	}
}

/*
 * Location: C:\Users\Shris\Desktop\新建文件夹
 * (5)\MESQMS(1).war!\WEB-INF\classes\com\mes\qms\server\service\po\sfc\
 * SFCTaskIPT.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.2
 */