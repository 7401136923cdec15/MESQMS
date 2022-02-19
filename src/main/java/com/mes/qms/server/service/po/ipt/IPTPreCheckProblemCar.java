package com.mes.qms.server.service.po.ipt;

import java.io.Serializable;

/**
 * 预检问题项-车
 */
public class IPTPreCheckProblemCar implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 车型
	 */
	public int ProductID = 0;
	public String ProductNo = "";
	/**
	 * 车号
	 */
	public String CarNumber = "";
	/**
	 * 修程
	 */
	public int LineID = 0;
	public String LineName = "";
	/**
	 * 局段
	 */
	public int CustomID = 0;
	public String CustomName = "";
	/**
	 * 订单
	 */
	public int OrderID = 0;
	public String OrderNo = "";
	/**
	 * 问题数量
	 */
	public int Totals = 0;
	public int ToDo = 0;

	public IPTPreCheckProblemCar() {
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

	public String getCarNumber() {
		return CarNumber;
	}

	public void setCarNumber(String carNumber) {
		CarNumber = carNumber;
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

	public int getCustomID() {
		return CustomID;
	}

	public void setCustomID(int customID) {
		CustomID = customID;
	}

	public String getCustomName() {
		return CustomName;
	}

	public void setCustomName(String customName) {
		CustomName = customName;
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

	public int getTotals() {
		return Totals;
	}

	public void setTotals(int totals) {
		Totals = totals;
	}

	public int getToDo() {
		return ToDo;
	}

	public void setToDo(int toDo) {
		ToDo = toDo;
	}
}

/*
 * Location: C:\Users\Shris\Desktop\新建文件夹
 * (5)\MESQMS(1).war!\WEB-INF\classes\com\mes\qms\server\service\po\ipt\
 * IPTPreCheckProblem.class Java compiler version: 8 (52.0) JD-Core Version:
 * 1.1.2
 */