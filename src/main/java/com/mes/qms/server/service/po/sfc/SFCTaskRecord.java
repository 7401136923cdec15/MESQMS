package com.mes.qms.server.service.po.sfc;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 专检存档
 * 
 * @author YouWang·Peng
 * @CreateTime 2021-10-20 13:46:39
 */
public class SFCTaskRecord implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 订单ID
	 */
	public int OrderID = 0;
	/**
	 * 工位ID
	 */
	public int PartID = 0;
	/**
	 * 1已存档
	 */
	public int Status = 0;
	/**
	 * 存档人ID
	 */
	public int CreateID = 0;
	/**
	 * 存档人名称
	 */
	public String Creator = "";
	/**
	 * 存档时刻
	 */
	public Calendar CreateTime = Calendar.getInstance();

	public SFCTaskRecord() {
		super();
	}

	public SFCTaskRecord(int iD, int orderID, int partID, int status, int createID, String creator,
			Calendar createTime) {
		super();
		ID = iD;
		OrderID = orderID;
		PartID = partID;
		Status = status;
		CreateID = createID;
		Creator = creator;
		CreateTime = createTime;
	}

	public int getID() {
		return ID;
	}

	public int getOrderID() {
		return OrderID;
	}

	public int getPartID() {
		return PartID;
	}

	public int getStatus() {
		return Status;
	}

	public int getCreateID() {
		return CreateID;
	}

	public String getCreator() {
		return Creator;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public void setPartID(int partID) {
		PartID = partID;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public void setCreateID(int createID) {
		CreateID = createID;
	}

	public void setCreator(String creator) {
		Creator = creator;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}
}
