package com.mes.qms.server.service.po.sfc;

import com.mes.qms.server.service.po.sfc.SFCTaskIPTPartNo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 车号区分检验任务(专检)
 */
public class SFCTaskIPTPartNo implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	public int OrderID = 0;
	public String OrderNo = "";
	/**
	 * 车号
	 */
	public String PartNo = "";
	/**
	 * 车号后四位
	 */
	public String No = "";
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
	 * 工序总数
	 */
	public int FQTY = 0;
	/**
	 * 任务详情
	 */
	public List<SFCTaskIPT> SFCTaskIPTList = new ArrayList<>();

	public String getPartNo() {
		return PartNo;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

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

	public List<SFCTaskIPT> getSFCTaskIPTList() {
		return SFCTaskIPTList;
	}

	public void setSFCTaskIPTList(List<SFCTaskIPT> sFCTaskIPTList) {
		SFCTaskIPTList = sFCTaskIPTList;
	}

	public String getNo() {
		return No;
	}

	public void setNo(String no) {
		No = no;
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

	public String getOrderNo() {
		return OrderNo;
	}

	public int getFQTY() {
		return FQTY;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public void setOrderNo(String orderNo) {
		OrderNo = orderNo;
	}

	public void setFQTY(int fQTY) {
		FQTY = fQTY;
	}
}
