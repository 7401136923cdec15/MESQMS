package com.mes.qms.server.service.po.pic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.mes.qms.server.service.po.oms.OMSOrder;

/**
 * 上传的车辆信息
 */
public class SFCCarInfo implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 订单号
	 */
	public int OrderID = 0;
	/**
	 * 车型
	 */
	public String ProductNo = "";
	/**
	 * 车号
	 */
	public String CarNo = "";
	/**
	 * 创建人ID
	 */
	public int CreaterID = 0;
	/**
	 * 创建时刻
	 */
	public Calendar CreateTime = Calendar.getInstance();
	/**
	 * 创建人
	 */
	public String Creator = "";
	/**
	 * 部位信息集合
	 */
	public List<SFCRankInfo> SFCRankInfoList = new ArrayList<SFCRankInfo>();

	// 辅助信息
	public OMSOrder Order = new OMSOrder();
	/**
	 * 上传进度
	 */
	public String Progress = "";
	/**
	 * 总数
	 */
	public int TotalSize = 0;
	/**
	 * 完成数
	 */
	public int FinishSize = 0;

	public SFCCarInfo() {
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	public String getCarNo() {
		return CarNo;
	}

	public void setCarNo(String carNo) {
		CarNo = carNo;
	}

	public int getOrderID() {
		return OrderID;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public int getCreaterID() {
		return CreaterID;
	}

	public void setCreaterID(int createrID) {
		CreaterID = createrID;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public String getCreator() {
		return Creator;
	}

	public void setCreator(String creator) {
		Creator = creator;
	}

	public List<SFCRankInfo> getSFCRankInfoList() {
		return SFCRankInfoList;
	}

	public void setSFCRankInfoList(List<SFCRankInfo> sFCRankInfoList) {
		SFCRankInfoList = sFCRankInfoList;
	}

	public OMSOrder getOrder() {
		return Order;
	}

	public void setOrder(OMSOrder order) {
		Order = order;
	}

	public String getProgress() {
		return Progress;
	}

	public void setProgress(String progress) {
		Progress = progress;
	}

	public int getTotalSize() {
		return TotalSize;
	}

	public void setTotalSize(int totalSize) {
		TotalSize = totalSize;
	}

	public int getFinishSize() {
		return FinishSize;
	}

	public void setFinishSize(int finishSize) {
		FinishSize = finishSize;
	}
}
