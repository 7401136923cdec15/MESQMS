package com.mes.qms.server.service.po.ipt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.mes.qms.server.service.po.bpm.BPMTaskBase;

/**
 * 预检报告
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-2-12 13:13:23
 * @LastEditTime 2020-2-12 13:13:27
 *
 */
public class IPTPreCheckReport extends BPMTaskBase implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 预检报告ID(主键)
	 */
//	public int ID = 0;
	/**
	 * 订单ID
	 */
	public int OrderID = 0;
	/**
	 * 订单号
	 */
	public String OrderNo = "";
	/**
	 * 车号
	 */
	public String PartNo = "";
	/**
	 * 修程
	 */
	public String LineName = "";
	/**
	 * 局段
	 */
	public String CustomerName = "";
	/**
	 * 预检项目集合
	 */
	public List<IPTPreCheckItem> IPTPreCheckItemList = new ArrayList<IPTPreCheckItem>();
	/**
	 * 创建人ID
	 */
	public int CreateID = 0;
	/**
	 * 创建人名称
	 */
	public String Creator = "";
	/**
	 * 创建时刻
	 */
	public Calendar CreateTime = Calendar.getInstance();
	/**
	 * 审批人ID
	 */
	public int AuditID = 0;
	/**
	 * 审批人名称
	 */
	public String Auditor = "";
	/**
	 * 审批时刻
	 */
	public Calendar AuditTime = Calendar.getInstance();
	/**
	 * 状态
	 */
//	public int Status = 0;

	public IPTPreCheckReport() {
		Calendar wBaseTime = Calendar.getInstance();
		wBaseTime.set(2000, 0, 1);
		AuditTime = wBaseTime;
		CreateTime = wBaseTime;
	}

	public String getPartNo() {
		return PartNo;
	}

	public int getOrderID() {
		return OrderID;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public String getOrderNo() {
		return OrderNo;
	}

	public void setOrderNo(String orderNo) {
		OrderNo = orderNo;
	}

	public String getLineName() {
		return LineName;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getCreateID() {
		return CreateID;
	}

	public void setCreateID(int createID) {
		CreateID = createID;
	}

	public String getCreator() {
		return Creator;
	}

	public void setCreator(String creator) {
		Creator = creator;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
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

	public Calendar getAuditTime() {
		return AuditTime;
	}

	public void setAuditTime(Calendar auditTime) {
		AuditTime = auditTime;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public String getCustomerName() {
		return CustomerName;
	}

	public void setCustomerName(String customerName) {
		CustomerName = customerName;
	}

	public List<IPTPreCheckItem> getIPTPreCheckItemList() {
		return IPTPreCheckItemList;
	}

	public void setIPTPreCheckItemList(List<IPTPreCheckItem> iPTPreCheckItemList) {
		IPTPreCheckItemList = iPTPreCheckItemList;
	}
}
