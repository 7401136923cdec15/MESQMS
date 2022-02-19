package com.mes.qms.server.service.po.tcm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 物料变更日志
 */
public class TCMMaterialChangeLog implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 创建时刻
	 */
	public Calendar CreateTime = Calendar.getInstance();
	/**
	 * 创建人ID
	 */
	public int CreateID = 0;
	/**
	 * 受影响的订单集合
	 */
	public String OrderIDList = "";
	/**
	 * 受影响的车辆集合
	 */
	public String PartNoList = "";
	/**
	 * 子项列表
	 */
	public List<TCMMaterialChangeItems> ItemList = new ArrayList<TCMMaterialChangeItems>();

	/**
	 * 车型
	 */
	public int ProductID = 0;
	/**
	 * 修程
	 */
	public int LineID = 0;
	/**
	 * 局段
	 */
	public int CustomerID = 0;

	public String ProductNo = "";
	public String LineName = "";
	public String Customer = "";

	// 2021-5-30 14:17:21 新增字段(变更单)
	/**
	 * 变更单号
	 */
	public String ChangeFormNo = "";
	/**
	 * 变更单访问地址
	 */
	public String ChangeFormUri = "";
	/**
	 * 变更人信息
	 */
	public String ChangeUser = "";
	/**
	 * 变更类型
	 */
	public String ChangeType = "";
	/**
	 * 是否发起工艺变更审批流程 0未发起 1已发起
	 */
	public int ShowStatus = 0;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public int getProductID() {
		return ProductID;
	}

	public String getChangeFormNo() {
		return ChangeFormNo;
	}

	public String getChangeFormUri() {
		return ChangeFormUri;
	}

	public String getChangeUser() {
		return ChangeUser;
	}

	public String getChangeType() {
		return ChangeType;
	}

	public void setChangeFormNo(String changeFormNo) {
		ChangeFormNo = changeFormNo;
	}

	public void setChangeFormUri(String changeFormUri) {
		ChangeFormUri = changeFormUri;
	}

	public void setChangeUser(String changeUser) {
		ChangeUser = changeUser;
	}

	public void setChangeType(String changeType) {
		ChangeType = changeType;
	}

	public int getLineID() {
		return LineID;
	}

	public int getCustomerID() {
		return CustomerID;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public String getLineName() {
		return LineName;
	}

	public String getCustomer() {
		return Customer;
	}

	public void setProductID(int productID) {
		ProductID = productID;
	}

	public void setLineID(int lineID) {
		LineID = lineID;
	}

	public void setCustomerID(int customerID) {
		CustomerID = customerID;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public void setCustomer(String customer) {
		Customer = customer;
	}

	public int getCreateID() {
		return CreateID;
	}

	public String getOrderIDList() {
		return OrderIDList;
	}

	public String getPartNoList() {
		return PartNoList;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public void setCreateID(int createID) {
		CreateID = createID;
	}

	public void setOrderIDList(String orderIDList) {
		OrderIDList = orderIDList;
	}

	public void setPartNoList(String partNoList) {
		PartNoList = partNoList;
	}

	public List<TCMMaterialChangeItems> getItemList() {
		return ItemList;
	}

	public void setItemList(List<TCMMaterialChangeItems> itemList) {
		ItemList = itemList;
	}

	public int getShowStatus() {
		return ShowStatus;
	}

	public void setShowStatus(int showStatus) {
		ShowStatus = showStatus;
	}
}
