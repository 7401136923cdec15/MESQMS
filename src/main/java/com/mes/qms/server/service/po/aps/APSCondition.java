package com.mes.qms.server.service.po.aps;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 排程条件
 * 
 * @author PengYouWang
 * @CreateTime 2020年1月7日20:40:38
 * @LastEditTime 2020年1月7日20:52:55
 *
 */
public class APSCondition implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 优先级集合列表
	 */
	public List<Integer> OMSOrderPrioritys = new ArrayList<Integer>();
	/**
	 * 修程优先级
	 */
	public List<Integer> LineOrders = new ArrayList<Integer>();
	/**
	 * 局段优先级
	 */
	public List<Integer> CustomerOrders = new ArrayList<Integer>();
	/**
	 * 产品型号优先级
	 */
	public List<Integer> ProductIDs = new ArrayList<Integer>();
	/**
	 * 跨天限制时长
	 */
	public int LimitMinutes = 0;
	/**
	 * 冗余天数
	 */
	public int RedundantDays = 0;
	/**
	 * 排程性质
	 */
	public int ShiftPeriod = 0;
	/**
	 * 编辑人
	 */
	public int EditID = 0;
	/**
	 * 编辑时刻
	 */
	public Calendar EditTime = Calendar.getInstance();

	public APSCondition() {
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public List<Integer> getOMSOrderPrioritys() {
		return OMSOrderPrioritys;
	}

	public void setOMSOrderPrioritys(List<Integer> oMSOrderPrioritys) {
		OMSOrderPrioritys = oMSOrderPrioritys;
	}

	public List<Integer> getLineOrders() {
		return LineOrders;
	}

	public void setLineOrders(List<Integer> lineOrders) {
		LineOrders = lineOrders;
	}

	public List<Integer> getCustomerOrders() {
		return CustomerOrders;
	}

	public void setCustomerOrders(List<Integer> wCustomerOrders) {
		this.CustomerOrders = wCustomerOrders;
	}

	public List<Integer> getProductIDs() {
		return ProductIDs;
	}

	public void setProductIDs(List<Integer> wProductIDs) {
		this.ProductIDs = wProductIDs;
	}

	public int getLimitMinutes() {
		return LimitMinutes;
	}

	public void setLimitMinutes(int limitMinutes) {
		LimitMinutes = limitMinutes;
	}

	public int getShiftPeriod() {
		return ShiftPeriod;
	}

	public void setShiftPeriod(int shiftPeriod) {
		ShiftPeriod = shiftPeriod;
	}

	public int getEditID() {
		return EditID;
	}

	public void setEditID(int editID) {
		EditID = editID;
	}

	public Calendar getEditTime() {
		return EditTime;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
	}
}
