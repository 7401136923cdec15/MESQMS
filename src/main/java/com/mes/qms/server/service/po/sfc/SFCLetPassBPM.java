package com.mes.qms.server.service.po.sfc;

import java.io.Serializable;

import com.mes.qms.server.service.po.bpm.BPMTaskBase;

/**
 * 例外放行
 */
public class SFCLetPassBPM extends BPMTaskBase implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 订单ID
	 */
	public int OrderID = 0;
	/**
	 * 工位ID
	 */
	public int PartID = 0;
	/**
	 * 关闭工位ID
	 */
	public int ClosePartID = 0;
	/**
	 * 工序ID集合，逗号隔开
	 */
	public String StepIDs = "";

	/**
	 * 描述信息
	 */
	public String DescribInfo = "";
	/**
	 * 图片，支持多个，逗号隔开
	 */
	public String ImageUrl = "";

	// 辅助属性
	public String ProductNo = "";
	public String PartNo = "";
	public String LineName = "";
	public String CustomerName = "";
	public String PartName = "";
	public String StepNames = "";
	public String ClosePartName = "";

	public int TagTypes = 0;

	public int getTagTypes() {
		return TagTypes;
	}

	public void setTagTypes(int tagTypes) {
		TagTypes = tagTypes;
	}

	public int getOrderID() {
		return OrderID;
	}

	public int getPartID() {
		return PartID;
	}

	public int getClosePartID() {
		return ClosePartID;
	}

	public String getStepIDs() {
		return StepIDs;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public String getPartNo() {
		return PartNo;
	}

	public String getLineName() {
		return LineName;
	}

	public String getCustomerName() {
		return CustomerName;
	}

	public String getPartName() {
		return PartName;
	}

	public String getStepNames() {
		return StepNames;
	}

	public String getClosePartName() {
		return ClosePartName;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public void setPartID(int partID) {
		PartID = partID;
	}

	public void setClosePartID(int closePartID) {
		ClosePartID = closePartID;
	}

	public void setStepIDs(String stepIDs) {
		StepIDs = stepIDs;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public void setCustomerName(String customerName) {
		CustomerName = customerName;
	}

	public void setPartName(String partName) {
		PartName = partName;
	}

	public void setStepNames(String stepNames) {
		StepNames = stepNames;
	}

	public void setClosePartName(String closePartName) {
		ClosePartName = closePartName;
	}

	public String getDescribInfo() {
		return DescribInfo;
	}

	public String getImageUrl() {
		return ImageUrl;
	}

	public void setDescribInfo(String describInfo) {
		DescribInfo = describInfo;
	}

	public void setImageUrl(String imageUrl) {
		ImageUrl = imageUrl;
	}
}
