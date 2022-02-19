package com.mes.qms.server.service.po.sfc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 临时性检查
 */
public class SFCTemporaryExamination implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 任务主键
	 */
	public int ID = 0;
	/**
	 * 单据编号
	 */
	public String Code = "";
	/**
	 * 订单号
	 */
	public int OrderID = 0;
	/**
	 * 当前检查工位
	 */
	public int PartID = 0;
	/**
	 * 包含的检查工位(包含当前检查工位)
	 */
	public List<Integer> PartIDList = new ArrayList<>();
	/**
	 * 状态
	 */
	public int Status = 0;
	/**
	 * 创建人ID(质量工程师)
	 */
	public int CreateID = 0;
	/**
	 * 创建人名称
	 */
	public Calendar CreateTime = Calendar.getInstance();
	/**
	 * 检查人员列表(工区检验员)
	 */
	public List<Integer> CheckIDList = new ArrayList<>();
	/**
	 * 备注
	 */
	public String Remark = "";
	/**
	 * 任务完成时刻
	 */
	public Calendar FinishTime = Calendar.getInstance();

	// 辅助信息
	public String OrderNo = "";
	public String WBSNo = "";
	public int ProductID = 0;
	public String ProductNo = "";
	public String PartNo = "";
	public int LineID = 0;
	public String LineName = "";
	public int CustomerID = 0;
	public String CustomerName = "";
	public String Creator = "";
	public String CheckNames = "";
	public String StatusText = "";
	public String PartName = "";
	public String PartNames = "";

	public int TagTypes = 0;

	public SFCTemporaryExamination() {
	}

	public SFCTemporaryExamination(int iD, String code, int orderID, int partID, List<Integer> partIDList, int status,
			int createID, Calendar createTime, List<Integer> checkIDList, String remark, Calendar finishTime,
			String orderNo, String wBSNo, int productID, String productNo, String partNo, int lineID, String lineName,
			int customerID, String customerName, String creator, String checkNames, String statusText, String partName,
			String partNames) {
		ID = iD;
		Code = code;
		OrderID = orderID;
		PartID = partID;
		PartIDList = partIDList;
		Status = status;
		CreateID = createID;
		CreateTime = createTime;
		CheckIDList = checkIDList;
		Remark = remark;
		FinishTime = finishTime;
		OrderNo = orderNo;
		WBSNo = wBSNo;
		ProductID = productID;
		ProductNo = productNo;
		PartNo = partNo;
		LineID = lineID;
		LineName = lineName;
		CustomerID = customerID;
		CustomerName = customerName;
		Creator = creator;
		CheckNames = checkNames;
		StatusText = statusText;
		PartName = partName;
		PartNames = partNames;
	}

	public Calendar getFinishTime() {
		return FinishTime;
	}

	public int getTagTypes() {
		return TagTypes;
	}

	public void setTagTypes(int tagTypes) {
		TagTypes = tagTypes;
	}

	public void setFinishTime(Calendar finishTime) {
		FinishTime = finishTime;
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

	public int getPartID() {
		return PartID;
	}

	public void setPartID(int partID) {
		PartID = partID;
	}

	public List<Integer> getPartIDList() {
		return PartIDList;
	}

	public void setPartIDList(List<Integer> partIDList) {
		PartIDList = partIDList;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public int getCreateID() {
		return CreateID;
	}

	public void setCreateID(int createID) {
		CreateID = createID;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public List<Integer> getCheckIDList() {
		return CheckIDList;
	}

	public void setCheckIDList(List<Integer> checkIDList) {
		CheckIDList = checkIDList;
	}

	public String getOrderNo() {
		return OrderNo;
	}

	public void setOrderNo(String orderNo) {
		OrderNo = orderNo;
	}

	public String getWBSNo() {
		return WBSNo;
	}

	public void setWBSNo(String wBSNo) {
		WBSNo = wBSNo;
	}

	public int getProductID() {
		return ProductID;
	}

	public void setProductID(int productID) {
		ProductID = productID;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	public String getPartNo() {
		return PartNo;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public int getLineID() {
		return LineID;
	}

	public void setLineID(int lineID) {
		LineID = lineID;
	}

	public String getLineName() {
		return LineName;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
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

	public String getCreator() {
		return Creator;
	}

	public void setCreator(String creator) {
		Creator = creator;
	}

	public String getCheckNames() {
		return CheckNames;
	}

	public void setCheckNames(String checkNames) {
		CheckNames = checkNames;
	}

	public String getStatusText() {
		return StatusText;
	}

	public void setStatusText(String statusText) {
		StatusText = statusText;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public String getPartName() {
		return PartName;
	}

	public void setPartName(String partName) {
		PartName = partName;
	}

	public String getPartNames() {
		return PartNames;
	}

	public void setPartNames(String partNames) {
		PartNames = partNames;
	}
}
