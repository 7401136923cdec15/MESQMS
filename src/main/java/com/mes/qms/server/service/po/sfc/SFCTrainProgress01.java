package com.mes.qms.server.service.po.sfc;

import java.io.Serializable;

/**
 * 车辆转序情况，第一层(车辆整体进度)
 */
public class SFCTrainProgress01 implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 订单ID
	 */
	public int OrderID = 0;
	/**
	 * 车号
	 */
	public String PartNo = "";
	/**
	 * 修程
	 */
	public String LineName = "";
	/**
	 * 局段
	 */
	public String Customer = "";
	/**
	 * 车型
	 */
	public String ProductNo = "";
	/**
	 * 工位总数
	 */
	public int StationNumber = 0;
	/**
	 * 工位完成数
	 */
	public int FinishedNumber = 0;

	public SFCTrainProgress01() {
	}

	public SFCTrainProgress01(int orderID, String partNo, String lineName, String customer, String productNo,
			int stationNumber, int finishedNumber) {
		super();
		OrderID = orderID;
		PartNo = partNo;
		LineName = lineName;
		Customer = customer;
		ProductNo = productNo;
		StationNumber = stationNumber;
		FinishedNumber = finishedNumber;
	}

	public int getOrderID() {
		return OrderID;
	}

	public String getPartNo() {
		return PartNo;
	}

	public String getLineName() {
		return LineName;
	}

	public String getCustomer() {
		return Customer;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public int getStationNumber() {
		return StationNumber;
	}

	public int getFinishedNumber() {
		return FinishedNumber;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public void setCustomer(String customer) {
		Customer = customer;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	public void setStationNumber(int stationNumber) {
		StationNumber = stationNumber;
	}

	public void setFinishedNumber(int finishedNumber) {
		FinishedNumber = finishedNumber;
	}
}
