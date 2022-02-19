package com.mes.qms.server.service.po.sfc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 临时性检查 工位子项
 */
public class SFCTemporaryExaminationPartItem implements Serializable {

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

	public int StepSize = 0;
	public int DoneStep = 0;

	public List<SFCTemporaryExaminationStepItem> StepList = new ArrayList<>();

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

	public int getStepSize() {
		return StepSize;
	}

	public void setStepSize(int stepSize) {
		StepSize = stepSize;
	}

	public int getDoneStep() {
		return DoneStep;
	}

	public void setDoneStep(int doneStep) {
		DoneStep = doneStep;
	}

	public List<SFCTemporaryExaminationStepItem> getStepList() {
		return StepList;
	}

	public void setStepList(List<SFCTemporaryExaminationStepItem> stepList) {
		StepList = stepList;
	}
}
