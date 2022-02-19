package com.mes.qms.server.service.po.sfc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.ipt.IPTItem;
import com.mes.qms.server.service.po.ipt.IPTValue;

/**
 * 临时性检查 工序子项
 */
public class SFCTemporaryExaminationStepItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int OrderID = 0;
	public String OrderNo = "";
	public int ProductID = 0;
	public String ProductNo = "";
	public String PartNo = "";
	public int LineID = 0;
	public String LineName = "";
	public int CustomerID = 0;
	public String CustomerName = "";

	public int Status = 0;

	public int PartID = 0;
	public String PartName = "";

	public int StepID = 0;
	public String StepName = "";

	public int ItemSize = 0;
	public int DoneItem = 0;

	public List<IPTItem> ToDoList = new ArrayList<>();
	public List<IPTItem> DoneList = new ArrayList<>();
	public List<IPTValue> ValueList = new ArrayList<>();

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

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public int getPartID() {
		return PartID;
	}

	public void setPartID(int partID) {
		PartID = partID;
	}

	public String getPartName() {
		return PartName;
	}

	public void setPartName(String partName) {
		PartName = partName;
	}

	public int getStepID() {
		return StepID;
	}

	public void setStepID(int stepID) {
		StepID = stepID;
	}

	public String getStepName() {
		return StepName;
	}

	public void setStepName(String stepName) {
		StepName = stepName;
	}

	public int getItemSize() {
		return ItemSize;
	}

	public void setItemSize(int itemSize) {
		ItemSize = itemSize;
	}

	public int getDoneItem() {
		return DoneItem;
	}

	public void setDoneItem(int doneItem) {
		DoneItem = doneItem;
	}

	public List<IPTItem> getToDoList() {
		return ToDoList;
	}

	public void setToDoList(List<IPTItem> toDoList) {
		ToDoList = toDoList;
	}

	public List<IPTItem> getDoneList() {
		return DoneList;
	}

	public void setDoneList(List<IPTItem> doneList) {
		DoneList = doneList;
	}

	public List<IPTValue> getValueList() {
		return ValueList;
	}

	public void setValueList(List<IPTValue> valueList) {
		ValueList = valueList;
	}
}
