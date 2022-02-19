package com.mes.qms.server.service.po.ipt;

import com.mes.qms.server.service.po.ipt.IPTOrderReportPartPoint;
import java.io.Serializable;

public class IPTOrderReportPartPoint implements Serializable {
	private static final long serialVersionUID = 1L;
	public int ID = 0;

	public int ReportPartID = 0;

	public int StepID = 0;

	public String StepName = "";

	public int Status = 0;

	public String StatusText = "";

	public int PartID = 0;

	public String PartName = "";

	public int Type = 0;

	public int OrderID = 0;

	public String OrderNo = "";

	public String PartNo = "";

	public int CustomerID = 0;

	public String CustomerName = "";

	public int LineID = 0;

	public String LineName = "";

	public IPTOrderReportPartPoint() {
	}

	public IPTOrderReportPartPoint(int iD, int reportPartID, int stepID, int status) {
		this.ID = iD;
		this.ReportPartID = reportPartID;
		this.StepID = stepID;
		this.Status = status;
	}

	public int getID() {
		return this.ID;
	}

	public void setID(int iD) {
		this.ID = iD;
	}

	public int getReportPartID() {
		return this.ReportPartID;
	}

	public void setReportPartID(int reportPartID) {
		this.ReportPartID = reportPartID;
	}

	public int getStepID() {
		return this.StepID;
	}

	public void setStepID(int stepID) {
		this.StepID = stepID;
	}

	public String getStepName() {
		return this.StepName;
	}

	public void setStepName(String stepName) {
		this.StepName = stepName;
	}

	public int getStatus() {
		return this.Status;
	}

	public void setStatus(int status) {
		this.Status = status;
	}

	public String getStatusText() {
		return this.StatusText;
	}

	public void setStatusText(String statusText) {
		this.StatusText = statusText;
	}

	public int getPartID() {
		return this.PartID;
	}

	public void setPartID(int partID) {
		this.PartID = partID;
	}

	public String getPartName() {
		return this.PartName;
	}

	public void setPartName(String partName) {
		this.PartName = partName;
	}

	public int getType() {
		return this.Type;
	}

	public void setType(int type) {
		this.Type = type;
	}

	public int getOrderID() {
		return this.OrderID;
	}

	public void setOrderID(int orderID) {
		this.OrderID = orderID;
	}

	public String getOrderNo() {
		return this.OrderNo;
	}

	public void setOrderNo(String orderNo) {
		this.OrderNo = orderNo;
	}

	public String getPartNo() {
		return this.PartNo;
	}

	public void setPartNo(String partNo) {
		this.PartNo = partNo;
	}

	public int getCustomerID() {
		return this.CustomerID;
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

	public int getLineID() {
		return this.LineID;
	}

	public void setLineID(int lineID) {
		this.LineID = lineID;
	}

	public String getLineName() {
		return this.LineName;
	}

	public void setLineName(String lineName) {
		this.LineName = lineName;
	}
}

/*
 * Location: C:\Users\Shris\Desktop\新建文件夹
 * (5)\MESQMS(1).war!\WEB-INF\classes\com\mes\qms\server\service\po\ipt\
 * IPTOrderReportPartPoint.class Java compiler version: 8 (52.0) JD-Core
 * Version: 1.1.2
 */