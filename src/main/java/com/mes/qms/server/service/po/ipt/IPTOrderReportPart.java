package com.mes.qms.server.service.po.ipt;

import com.mes.qms.server.service.po.ipt.IPTOrderReportPart;
import com.mes.qms.server.service.po.ipt.IPTOrderReportPartPoint;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IPTOrderReportPart implements Serializable {
	private static final long serialVersionUID = 1L;
	public int ID = 0;

	public int ReportID = 0;

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

	public List<IPTOrderReportPartPoint> IPTOrderReportPartPointList = new ArrayList<>();

	public IPTOrderReportPart() {
	}

	public IPTOrderReportPart(int iD, int reportID, int partID, int type) {
		this.ID = iD;
		this.ReportID = reportID;
		this.PartID = partID;
		this.Type = type;
	}

	public int getID() {
		return this.ID;
	}

	public void setID(int iD) {
		this.ID = iD;
	}

	public int getReportID() {
		return this.ReportID;
	}

	public List<IPTOrderReportPartPoint> getIPTOrderReportPartPointList() {
		return this.IPTOrderReportPartPointList;
	}

	public void setIPTOrderReportPartPointList(List<IPTOrderReportPartPoint> iPTOrderReportPartPointList) {
		this.IPTOrderReportPartPointList = iPTOrderReportPartPointList;
	}

	public void setReportID(int reportID) {
		this.ReportID = reportID;
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
 * IPTOrderReportPart.class Java compiler version: 8 (52.0) JD-Core Version:
 * 1.1.2
 */