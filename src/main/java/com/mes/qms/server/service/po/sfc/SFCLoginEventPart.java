package com.mes.qms.server.service.po.sfc;

import java.io.Serializable;

/**
 * 打卡记录(工位分组)
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-12-1 21:42:00
 * @LastEditTime 2020-12-1 21:42:04
 *
 */
public class SFCLoginEventPart implements Serializable {
	private static final long serialVersionUID = 1L;

	public int OrderID = 0;
	public String PartNo = "";
	public int PartID = 0;
	public String PartName = "";

	public String LineName = "";

	public int StepSize = 0;
	public int ClockSize = 0;

	public int IsTurnOrder = 0;

	public int OrderNum = 0;

	public String Tip = "";

	public int getOrderID() {
		return OrderID;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public String getPartNo() {
		return PartNo;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
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

	public String getLineName() {
		return LineName;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public int getStepSize() {
		return StepSize;
	}

	public void setStepSize(int stepSize) {
		StepSize = stepSize;
	}

	public int getClockSize() {
		return ClockSize;
	}

	public void setClockSize(int clockSize) {
		ClockSize = clockSize;
	}

	public int getIsTurnOrder() {
		return IsTurnOrder;
	}

	public void setIsTurnOrder(int isTurnOrder) {
		IsTurnOrder = isTurnOrder;
	}

	public int getOrderNum() {
		return OrderNum;
	}

	public void setOrderNum(int orderNum) {
		OrderNum = orderNum;
	}

	public String getTip() {
		return Tip;
	}

	public void setTip(String tip) {
		Tip = tip;
	}
}
