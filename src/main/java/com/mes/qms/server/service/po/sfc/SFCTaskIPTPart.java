package com.mes.qms.server.service.po.sfc;

import com.mes.qms.server.service.po.sfc.SFCTaskIPTPart;
import java.io.Serializable;

/**
 * 工位区分检验任务(专检)
 */
public class SFCTaskIPTPart implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
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
	 * 工位ID
	 */
	public int PartID = 0;
	/**
	 * 工位
	 */
	public String PartName = "";
	/**
	 * 局段
	 */
	public String Customer = "";
	/**
	 * 修程
	 */
	public String LineName = "";

	/**
	 * 任务总数
	 */
	public int FQTYTotal = 0;
	/**
	 * 待做任务数
	 */
	public int FQTYToDo = 0;
	/**
	 * 已做任务数
	 */
	public int FQTYDone = 0;

	/**
	 * 顺序ID
	 */
	public int OrderNum = 0;
	public int FQTY = 0;

	public int getFQTYTotal() {
		return FQTYTotal;
	}

	public void setFQTYTotal(int fQTYTotal) {
		FQTYTotal = fQTYTotal;
	}

	public int getFQTYToDo() {
		return FQTYToDo;
	}

	public void setFQTYToDo(int fQTYToDo) {
		FQTYToDo = fQTYToDo;
	}

	public int getFQTYDone() {
		return FQTYDone;
	}

	public void setFQTYDone(int fQTYDone) {
		FQTYDone = fQTYDone;
	}

	public String getCustomer() {
		return Customer;
	}

	public void setCustomer(String customer) {
		Customer = customer;
	}

	public String getLineName() {
		return LineName;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
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

	public String getPartNo() {
		return PartNo;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public int getOrderNum() {
		return OrderNum;
	}

	public void setOrderNum(int orderNum) {
		OrderNum = orderNum;
	}

	public int getFQTY() {
		return FQTY;
	}

	public void setFQTY(int fQTY) {
		FQTY = fQTY;
	}
}
