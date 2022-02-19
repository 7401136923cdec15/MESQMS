package com.mes.qms.server.service.po.ipt;

import com.mes.qms.server.service.po.ipt.IPTOrderReport;
import com.mes.qms.server.service.po.ipt.IPTOrderReportPart;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IPTOrderReport implements Serializable {
	private static final long serialVersionUID = 1L;
	public int ID = 0;

	public int OrderID = 0;

	public String OrderNo = "";

	public String PartNo = "";

	public int CustomerID = 0;

	public String CustomerName = "";

	public int LineID = 0;

	public String LineName = "";

	public int CreateID = 0;

	public String Creator = "";

	public Calendar CreateTime = Calendar.getInstance();

	public int EditID = 0;

	public String Editor = "";

	public Calendar EditTime = Calendar.getInstance();

	public List<IPTOrderReportPart> IPTOrderReportPartList = new ArrayList<>();

	public IPTOrderReport() {
		this.CreateTime.set(2000, 0, 1);
		this.EditTime.set(2000, 0, 1);
	}

	public IPTOrderReport(int iD, int orderID, String orderNo, String partNo, int customerID, int lineID, int createID,
			Calendar createTime, int editID, Calendar editTime) {
		Calendar wBaseTime = Calendar.getInstance();
		wBaseTime.set(2000, 0, 1);
		if (createTime == null) {
			createTime = wBaseTime;
		}
		if (editTime == null) {
			editTime = wBaseTime;
		}
		this.ID = iD;
		this.OrderID = orderID;
		this.OrderNo = orderNo;
		this.PartNo = partNo;
		this.CustomerID = customerID;
		this.LineID = lineID;
		this.CreateID = createID;
		this.CreateTime = createTime;
		this.EditID = editID;
		this.EditTime = editTime;
	}

	public int getID() {
		return this.ID;
	}

	public void setID(int iD) {
		this.ID = iD;
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

	public List<IPTOrderReportPart> getIPTOrderReportPartList() {
		return this.IPTOrderReportPartList;
	}

	public void setIPTOrderReportPartList(List<IPTOrderReportPart> iPTOrderReportPartList) {
		this.IPTOrderReportPartList = iPTOrderReportPartList;
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

	public int getCreateID() {
		return this.CreateID;
	}

	public void setCreateID(int createID) {
		this.CreateID = createID;
	}

	public String getCreator() {
		return this.Creator;
	}

	public void setCreator(String creator) {
		this.Creator = creator;
	}

	public Calendar getCreateTime() {
		return this.CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		this.CreateTime = createTime;
	}

	public int getEditID() {
		return this.EditID;
	}

	public void setEditID(int editID) {
		this.EditID = editID;
	}

	public String getEditor() {
		return this.Editor;
	}

	public void setEditor(String editor) {
		this.Editor = editor;
	}

	public Calendar getEditTime() {
		return this.EditTime;
	}

	public void setEditTime(Calendar editTime) {
		this.EditTime = editTime;
	}
}

/*
 * Location: C:\Users\Shris\Desktop\新建文件夹
 * (5)\MESQMS(1).war!\WEB-INF\classes\com\mes\qms\server\service\po\ipt\
 * IPTOrderReport.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.2
 */