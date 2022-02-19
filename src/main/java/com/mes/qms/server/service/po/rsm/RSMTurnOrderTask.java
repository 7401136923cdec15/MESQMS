package com.mes.qms.server.service.po.rsm;

import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.po.rsm.RSMTurnOrderTask;
import java.io.Serializable;
import java.util.Calendar;

/**
 * 转序单
 */
public class RSMTurnOrderTask implements Serializable {
	private static final long serialVersionUID = 1L;
	public int ID = 0;

	public int ApplyID = 0;

	public Calendar ApplyTime = Calendar.getInstance();

	public int OrderID = 0;

	public int ApplyStationID = 0;

	public int TargetStationID = 0;

	public int Status = 0;

	public int TaskPartID = 0;

	public String ApplyName = "";

	public String OrderNo = "";

	public String CarNo = "";

	public String ApplyStationName = "";

	public String TargetStationName = "";

	public String StatusText = "";

	public int Type = 0;

	public String Remark = "";

	public Calendar FinishTime = Calendar.getInstance();

	public int ConfirmID = 0;

	public String ConfirmName = "";

	public Calendar ConfirmTime = Calendar.getInstance();

	public OMSOrder OMSOrder = new OMSOrder();

	public RSMTurnOrderTask() {
		this.FinishTime.set(2000, 0, 1, 0, 0, 0);
		this.ConfirmTime.set(2000, 0, 1, 0, 0, 0);
	}

	public int getID() {
		return this.ID;
	}

	public void setID(int iD) {
		this.ID = iD;
	}

	public String getRemark() {
		return this.Remark;
	}

	public void setRemark(String remark) {
		this.Remark = remark;
	}

	public int getApplyID() {
		return this.ApplyID;
	}

	public void setApplyID(int applyID) {
		this.ApplyID = applyID;
	}

	public Calendar getApplyTime() {
		return this.ApplyTime;
	}

	public void setApplyTime(Calendar applyTime) {
		this.ApplyTime = applyTime;
	}

	public int getOrderID() {
		return this.OrderID;
	}

	public void setOrderID(int orderID) {
		this.OrderID = orderID;
	}

	public int getApplyStationID() {
		return this.ApplyStationID;
	}

	public void setApplyStationID(int applyStationID) {
		this.ApplyStationID = applyStationID;
	}

	public Calendar getFinishTime() {
		return this.FinishTime;
	}

	public void setFinishTime(Calendar finishTime) {
		this.FinishTime = finishTime;
	}

	public int getTargetStationID() {
		return this.TargetStationID;
	}

	public void setTargetStationID(int targetStationID) {
		this.TargetStationID = targetStationID;
	}

	public int getStatus() {
		return this.Status;
	}

	public void setStatus(int status) {
		this.Status = status;
	}

	public String getApplyName() {
		return this.ApplyName;
	}

	public void setApplyName(String applyName) {
		this.ApplyName = applyName;
	}

	public String getOrderNo() {
		return this.OrderNo;
	}

	public void setOrderNo(String orderNo) {
		this.OrderNo = orderNo;
	}

	public String getCarNo() {
		return this.CarNo;
	}

	public void setCarNo(String carNo) {
		this.CarNo = carNo;
	}

	public String getApplyStationName() {
		return this.ApplyStationName;
	}

	public void setApplyStationName(String applyStationName) {
		this.ApplyStationName = applyStationName;
	}

	public String getTargetStationName() {
		return this.TargetStationName;
	}

	public void setTargetStationName(String targetStationName) {
		this.TargetStationName = targetStationName;
	}

	public String getStatusText() {
		return this.StatusText;
	}

	public void setStatusText(String statusText) {
		this.StatusText = statusText;
	}

	public int getType() {
		return this.Type;
	}

	public void setType(int type) {
		this.Type = type;
	}

	public int getTaskPartID() {
		return this.TaskPartID;
	}

	public void setTaskPartID(int taskPartID) {
		this.TaskPartID = taskPartID;
	}

	public int getConfirmID() {
		return ConfirmID;
	}

	public void setConfirmID(int confirmID) {
		ConfirmID = confirmID;
	}

	public Calendar getConfirmTime() {
		return ConfirmTime;
	}

	public void setConfirmTime(Calendar confirmTime) {
		ConfirmTime = confirmTime;
	}

	public String getConfirmName() {
		return ConfirmName;
	}

	public void setConfirmName(String confirmName) {
		ConfirmName = confirmName;
	}

	public OMSOrder getOMSOrder() {
		return OMSOrder;
	}

	public void setOMSOrder(OMSOrder oMSOrder) {
		OMSOrder = oMSOrder;
	}
}

/*
 * Location: C:\Users\Shris\Desktop\新建文件夹
 * (5)\MESQMS(1).war!\WEB-INF\classes\com\mes\qms\server\service\po\rsm\
 * RSMTurnOrderTask.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.2
 */