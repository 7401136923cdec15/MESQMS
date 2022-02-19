package com.mes.qms.server.service.po.aps;

import java.io.Serializable;
import java.util.Calendar;

public class APSMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	public int ID = 0; // 错误ID

	public int OrderID = 0;

	/**
	 * 错误类型 1物料 2 加工能力 3工艺等待
	 */
	public int Type = 0; // 错误类型

	public String ProductNo = ""; // 产品规格

	public int WorkShopID = 0; // 车间ID
	public int LineID = 0; // 产线ID
	public int PartID = 0; // 工序段ID
	public String PartName = "";
	public String MessageText = ""; // 异常描述

	/**
	 * 创建时刻
	 */
	public Calendar SubmitTime = Calendar.getInstance();
	/**
	 * 唯一ID(用于前端显示甘特图)
	 */
	public int UniqueID;

	public APSMessage() {
		this.ID = 0;
		this.Type = 0;
		this.ProductNo = "";
		this.WorkShopID = 0;
		this.LineID = 0;
		this.PartID = 0;
		this.PartName = "";
		this.MessageText = "";

		SubmitTime.set(2000, 1, 1);
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

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	public int getWorkShopID() {
		return WorkShopID;
	}

	public void setWorkShopID(int workShopID) {
		WorkShopID = workShopID;
	}

	public int getLineID() {
		return LineID;
	}

	public void setLineID(int lineID) {
		LineID = lineID;
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

	public String getMessageText() {
		return MessageText;
	}

	public void setMessageText(String messageText) {
		MessageText = messageText;
	}

	public Calendar getCreateTime() {
		return SubmitTime;
	}

	public void setCreateTime(Calendar createTime) {
		SubmitTime = createTime;
	}
}
