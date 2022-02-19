package com.mes.qms.server.service.po.mss;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 部件入库、拆解、维修记录
 * 
 * @author YouWang·Peng
 * @CreateTime 2021-6-18 09:55:10
 */
public class MSSPartRecord implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	public int ID = 0;
	/**
	 * 车型ID
	 */
	public int ProductID = 0;
	/**
	 * 车型
	 */
	public String ProductNo = "";
	/**
	 * 修程ID
	 */
	public int LineID = 0;
	/**
	 * 修程
	 */
	public String LineName = "";
	/**
	 * 局段ID
	 */
	public int CustomerID = 0;
	/**
	 * 局段
	 */
	public String Customer = "";
	/**
	 * 工位ID
	 */
	public int PartID = 0;
	/**
	 * 工位
	 */
	public String PartName = "";
	/**
	 * 工序ID
	 */
	public int StepID = 0;
	/**
	 * 工序
	 */
	public String StepName = "";
	/**
	 * 项点ID
	 */
	public int ItemID = 0;
	/**
	 * 项点名称
	 */
	public String ItemName = "";
	/**
	 * 部件编码
	 */
	public String MSSPartCode = "";
	/**
	 * 真实部件号
	 */
	public String MSSPartSerial = "";
	/**
	 * 订单ID
	 */
	public int OrderID = 0;
	/**
	 * 车号
	 */
	public String PartNo = "";
	/**
	 * 组装车的订单ID
	 */
	public int TartgetOrderID = 0;
	/**
	 * 组装车的车号
	 */
	public String TargetPartNo = "";
	/**
	 * 操作类型ID
	 */
	public int OperateType = 0;
	/**
	 * 操作类型名称
	 */
	public String OpereateTypeName = "";
	/**
	 * 操作人ID
	 */
	public int OperateID = 0;
	/**
	 * 操作人名称
	 */
	public String Operator = "";
	/**
	 * 操作时间
	 */
	public Calendar CreateTime = Calendar.getInstance();

	public MSSPartRecord() {

	}

	public MSSPartRecord(int iD, int productID, String productNo, int lineID, String lineName, int customerID,
			String customer, int partID, String partName, int stepID, String stepName, int itemID, String itemName,
			String mSSPartCode, String mSSPartSerial, int orderID, String partNo, int tartgetOrderID,
			String targetPartNo, int operateType, String opereateTypeName, int operateID, String operator,
			Calendar createTime) {
		super();
		ID = iD;
		ProductID = productID;
		ProductNo = productNo;
		LineID = lineID;
		LineName = lineName;
		CustomerID = customerID;
		Customer = customer;
		PartID = partID;
		PartName = partName;
		StepID = stepID;
		StepName = stepName;
		ItemID = itemID;
		ItemName = itemName;
		MSSPartCode = mSSPartCode;
		MSSPartSerial = mSSPartSerial;
		OrderID = orderID;
		PartNo = partNo;
		TartgetOrderID = tartgetOrderID;
		TargetPartNo = targetPartNo;
		OperateType = operateType;
		OpereateTypeName = opereateTypeName;
		OperateID = operateID;
		Operator = operator;
		CreateTime = createTime;
	}

	public int getID() {
		return ID;
	}

	public int getProductID() {
		return ProductID;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public int getLineID() {
		return LineID;
	}

	public String getLineName() {
		return LineName;
	}

	public int getCustomerID() {
		return CustomerID;
	}

	public String getCustomer() {
		return Customer;
	}

	public int getPartID() {
		return PartID;
	}

	public String getPartName() {
		return PartName;
	}

	public int getStepID() {
		return StepID;
	}

	public String getStepName() {
		return StepName;
	}

	public int getItemID() {
		return ItemID;
	}

	public String getItemName() {
		return ItemName;
	}

	public String getMSSPartCode() {
		return MSSPartCode;
	}

	public String getMSSPartSerial() {
		return MSSPartSerial;
	}

	public int getOrderID() {
		return OrderID;
	}

	public String getPartNo() {
		return PartNo;
	}

	public int getTartgetOrderID() {
		return TartgetOrderID;
	}

	public String getTargetPartNo() {
		return TargetPartNo;
	}

	public int getOperateType() {
		return OperateType;
	}

	public String getOpereateTypeName() {
		return OpereateTypeName;
	}

	public int getOperateID() {
		return OperateID;
	}

	public String getOperator() {
		return Operator;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public void setProductID(int productID) {
		ProductID = productID;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	public void setLineID(int lineID) {
		LineID = lineID;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public void setCustomerID(int customerID) {
		CustomerID = customerID;
	}

	public void setCustomer(String customer) {
		Customer = customer;
	}

	public void setPartID(int partID) {
		PartID = partID;
	}

	public void setPartName(String partName) {
		PartName = partName;
	}

	public void setStepID(int stepID) {
		StepID = stepID;
	}

	public void setStepName(String stepName) {
		StepName = stepName;
	}

	public void setItemID(int itemID) {
		ItemID = itemID;
	}

	public void setItemName(String itemName) {
		ItemName = itemName;
	}

	public void setMSSPartCode(String mSSPartCode) {
		MSSPartCode = mSSPartCode;
	}

	public void setMSSPartSerial(String mSSPartSerial) {
		MSSPartSerial = mSSPartSerial;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public void setTartgetOrderID(int tartgetOrderID) {
		TartgetOrderID = tartgetOrderID;
	}

	public void setTargetPartNo(String targetPartNo) {
		TargetPartNo = targetPartNo;
	}

	public void setOperateType(int operateType) {
		OperateType = operateType;
	}

	public void setOpereateTypeName(String opereateTypeName) {
		OpereateTypeName = opereateTypeName;
	}

	public void setOperateID(int operateID) {
		OperateID = operateID;
	}

	public void setOperator(String operator) {
		Operator = operator;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}
}
