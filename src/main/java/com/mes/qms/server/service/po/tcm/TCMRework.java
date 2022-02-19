package com.mes.qms.server.service.po.tcm;

import java.io.Serializable;

import com.mes.qms.server.service.po.bpm.BPMTaskBase;

/**
 * 工艺变更(返工)
 */
public class TCMRework extends BPMTaskBase implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 任务标记
	 */
	public int TagTypes = 0;
	/**
	 * 车型
	 */
	public int ProductID = 0;
	public String ProductNo = "";
	/**
	 * 局段
	 */
	public int CustomerID = 0;
	public String Customer = "";
	/**
	 * 修程
	 */
	public int LineID = 0;
	public String LineName = "";
	/**
	 * 车号
	 */
	public String PartNo = "";
	/**
	 * 返工内容
	 */
	public String Content = "";
	/**
	 * 工位
	 */
	public int PartID = 0;
	public String PartName = "";
	/**
	 * 工序
	 */
	public int PartPointID = 0;
	public String StepName = "";

	/**
	 * 辅助属性
	 */
	public String MonitorList = "";

	public String ProductNo_txt_ = "";
	public String LineName_txt_ = "";
	public String Customer_txt_ = "";
	public String PartNo_txt_ = "";
	public String PartName_txt_ = "";
	public String StepName_txt_ = "";
	public String Content_txt_ = "";

	public int getTagTypes() {
		return TagTypes;
	}

	public void setTagTypes(int tagTypes) {
		TagTypes = tagTypes;
	}

	public int getProductID() {
		return ProductID;
	}

	public String getMonitorList() {
		return MonitorList;
	}

	public void setMonitorList(String monitorList) {
		MonitorList = monitorList;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public int getCustomerID() {
		return CustomerID;
	}

	public String getCustomer() {
		return Customer;
	}

	public String getProductNo_txt_() {
		return ProductNo_txt_;
	}

	public String getLineName_txt_() {
		return LineName_txt_;
	}

	public String getCustomer_txt_() {
		return Customer_txt_;
	}

	public String getPartNo_txt_() {
		return PartNo_txt_;
	}

	public String getPartName_txt_() {
		return PartName_txt_;
	}

	public String getStepName_txt_() {
		return StepName_txt_;
	}

	public String getContent_txt_() {
		return Content_txt_;
	}

	public void setProductNo_txt_(String productNo_txt_) {
		ProductNo_txt_ = productNo_txt_;
	}

	public void setLineName_txt_(String lineName_txt_) {
		LineName_txt_ = lineName_txt_;
	}

	public void setCustomer_txt_(String customer_txt_) {
		Customer_txt_ = customer_txt_;
	}

	public void setPartNo_txt_(String partNo_txt_) {
		PartNo_txt_ = partNo_txt_;
	}

	public void setPartName_txt_(String partName_txt_) {
		PartName_txt_ = partName_txt_;
	}

	public void setStepName_txt_(String stepName_txt_) {
		StepName_txt_ = stepName_txt_;
	}

	public void setContent_txt_(String content_txt_) {
		Content_txt_ = content_txt_;
	}

	public int getLineID() {
		return LineID;
	}

	public String getLineName() {
		return LineName;
	}

	public String getPartNo() {
		return PartNo;
	}

	public String getContent() {
		return Content;
	}

	public void setProductID(int productID) {
		ProductID = productID;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	public void setCustomerID(int customerID) {
		CustomerID = customerID;
	}

	public void setCustomer(String customer) {
		Customer = customer;
	}

	public void setLineID(int lineID) {
		LineID = lineID;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public void setContent(String content) {
		Content = content;
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

	public void setPartID(int partID) {
		PartID = partID;
	}

	public int getPartPointID() {
		return PartPointID;
	}

	public void setPartPointID(int partPointID) {
		PartPointID = partPointID;
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
}
