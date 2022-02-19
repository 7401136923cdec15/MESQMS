package com.mes.qms.server.service.po.sfc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.bpm.BPMTaskBase;

/**
 * 试运申请
 */
public class SFCAttemptRun extends BPMTaskBase implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	public int OrderID = 0;

	public String WBSNo = "";

	public int ProductID = 0;

	public String ProductNo = "";

	public String PartNo = "";

	public int CheckerID = 0;

	public String CheckerName = "";

	public int PartID; // 台车当前工位

	public String PartName = ""; // Name+(No)

	/**
	 * 未完成的返修项
	 */
	public List<SFCRepairItem> ItemList = new ArrayList<SFCRepairItem>();

	public int getOrderID() {
		return OrderID;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
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

	public int getCheckerID() {
		return CheckerID;
	}

	public void setCheckerID(int checkerID) {
		CheckerID = checkerID;
	}

	public String getCheckerName() {
		return CheckerName;
	}

	public void setCheckerName(String checkerName) {
		CheckerName = checkerName;
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

	public List<SFCRepairItem> getItemList() {
		return ItemList;
	}

	public void setItemList(List<SFCRepairItem> itemList) {
		ItemList = itemList;
	}
}
