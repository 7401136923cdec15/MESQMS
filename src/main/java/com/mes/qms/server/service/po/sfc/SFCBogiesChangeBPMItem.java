package com.mes.qms.server.service.po.sfc;

import java.io.Serializable;

import com.mes.qms.server.service.po.oms.OMSOrder;

/**
 * 转向架车体互换子表
 */
public class SFCBogiesChangeBPMItem implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 主单据ID
	 */
	public int TaskID = 0;
	/**
	 * 车体订单
	 */
	public int BodyOrderID = 0;
	/**
	 * 转向架订单
	 */
	public int BogiesOrderID = 0;
	/**
	 * 车体订单的转向架号
	 */
	public String BodyBogiesNo = "";
	/**
	 * 转向架订单的转向架号
	 */
	public String BogiesBogiesNo = "";
	/**
	 * 车体订单的驱动号
	 */
	public String BodyDriveNo = "";
	/**
	 * 转向架订单的驱动号
	 */
	public String BogiesDriveNo = "";

	// ①辅助属性
	public OMSOrder BodyOrder = new OMSOrder();
	public OMSOrder BogiesOrder = new OMSOrder();

	public int getID() {
		return ID;
	}

	public int getTaskID() {
		return TaskID;
	}

	public int getBodyOrderID() {
		return BodyOrderID;
	}

	public int getBogiesOrderID() {
		return BogiesOrderID;
	}

	public String getBodyBogiesNo() {
		return BodyBogiesNo;
	}

	public String getBogiesBogiesNo() {
		return BogiesBogiesNo;
	}

	public String getBodyDriveNo() {
		return BodyDriveNo;
	}

	public String getBogiesDriveNo() {
		return BogiesDriveNo;
	}

	public void setBodyBogiesNo(String bodyBogiesNo) {
		BodyBogiesNo = bodyBogiesNo;
	}

	public void setBogiesBogiesNo(String bogiesBogiesNo) {
		BogiesBogiesNo = bogiesBogiesNo;
	}

	public void setBodyDriveNo(String bodyDriveNo) {
		BodyDriveNo = bodyDriveNo;
	}

	public void setBogiesDriveNo(String bogiesDriveNo) {
		BogiesDriveNo = bogiesDriveNo;
	}

	public OMSOrder getBodyOrder() {
		return BodyOrder;
	}

	public OMSOrder getBogiesOrder() {
		return BogiesOrder;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public void setTaskID(int taskID) {
		TaskID = taskID;
	}

	public void setBodyOrderID(int bodyOrderID) {
		BodyOrderID = bodyOrderID;
	}

	public void setBogiesOrderID(int bogiesOrderID) {
		BogiesOrderID = bogiesOrderID;
	}

	public void setBodyOrder(OMSOrder bodyOrder) {
		BodyOrder = bodyOrder;
	}

	public void setBogiesOrder(OMSOrder bogiesOrder) {
		BogiesOrder = bogiesOrder;
	}
}
