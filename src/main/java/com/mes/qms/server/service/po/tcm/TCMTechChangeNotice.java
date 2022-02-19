package com.mes.qms.server.service.po.tcm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.bpm.BPMTaskBase;

/**
 * 工艺变更通知(审批)
 */
public class TCMTechChangeNotice extends BPMTaskBase implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 改变日志ID
	 */
	public int ChangeLogID = 0;

	public int TagTypes = 0;

	/**
	 * 要处理的订单
	 */
	public List<Integer> OrderList = new ArrayList<Integer>();
	/**
	 * 不处理的车辆列表
	 */
	public String NoHandleOrders = "";

	/**
	 * 辅助属性
	 */
	public TCMMaterialChangeLog TCMMaterialChangeLog = new TCMMaterialChangeLog();

	/**
	 * 传输日志ID
	 */
	public int LogID = 0;

	/**
	 * 所有订单ID集合
	 */
	public List<Integer> AllOrderList = new ArrayList<Integer>();

	public int getTagTypes() {
		return TagTypes;
	}

	public void setTagTypes(int tagTypes) {
		TagTypes = tagTypes;
	}

	public int getChangeLogID() {
		return ChangeLogID;
	}

	public List<Integer> getOrderList() {
		return OrderList;
	}

	public TCMMaterialChangeLog getTCMMaterialChangeLog() {
		return TCMMaterialChangeLog;
	}

	public void setOrderList(List<Integer> orderList) {
		OrderList = orderList;
	}

	public void setTCMMaterialChangeLog(TCMMaterialChangeLog tCMMaterialChangeLog) {
		TCMMaterialChangeLog = tCMMaterialChangeLog;
	}

	public void setChangeLogID(int changeLogID) {
		ChangeLogID = changeLogID;
	}

	public String getNoHandleOrders() {
		return NoHandleOrders;
	}

	public void setNoHandleOrders(String noHandleOrders) {
		NoHandleOrders = noHandleOrders;
	}

	public int getLogID() {
		return LogID;
	}

	public void setLogID(int logID) {
		LogID = logID;
	}

	public List<Integer> getAllOrderList() {
		return AllOrderList;
	}

	public void setAllOrderList(List<Integer> allOrderList) {
		AllOrderList = allOrderList;
	}
}
