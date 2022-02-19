package com.mes.qms.server.service.po.record;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 导出校验记录
 * 
 * @author PengYouWang
 * @CreateTime 2020-10-26 17:51:12
 * @LastEditTime 2020-10-26 17:51:17
 *
 */
public class IPTExportCheckRecord implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 唯一编号
	 */
	public int ID = 0;
	/**
	 * 唯一编码
	 */
	public String Code = "";
	/**
	 * 操作员ID
	 */
	public int OperateID = 0;
	/**
	 * 操作员名称
	 */
	public String Operator = "";
	/**
	 * 操作时刻
	 */
	public Calendar CreateTime = Calendar.getInstance();
	/**
	 * 订单ID
	 */
	public int OrderID = 0;
	/**
	 * 车号
	 */
	public String PartNo = "";
	/**
	 * 大进度总数
	 */
	public int TotalSize = 0;
	/**
	 * 大进度当前进度值
	 */
	public int TotalProgress = 0;
	/**
	 * 大进度提示
	 */
	public String TotalTip = "";
	/**
	 * 小进度总数
	 */
	public int ItemSize = 0;
	/**
	 * 小进度当前进度值
	 */
	public int ItemProgress = 0;
	/**
	 * 小进度提示
	 */
	public String ItemTip = "";

	/**
	 * 结果集合
	 */
	public List<IPTCheckResult> IPTCheckResultList = new ArrayList<IPTCheckResult>();

	public IPTExportCheckRecord() {
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public int getOperateID() {
		return OperateID;
	}

	public void setOperateID(int operateID) {
		OperateID = operateID;
	}

	public String getOperator() {
		return Operator;
	}

	public void setOperator(String operator) {
		Operator = operator;
	}

	public int getOrderID() {
		return OrderID;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public String getPartNo() {
		return PartNo;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public int getTotalSize() {
		return TotalSize;
	}

	public void setTotalSize(int totalSize) {
		TotalSize = totalSize;
	}

	public int getTotalProgress() {
		return TotalProgress;
	}

	public void setTotalProgress(int totalProgress) {
		TotalProgress = totalProgress;
	}

	public String getTotalTip() {
		return TotalTip;
	}

	public void setTotalTip(String totalTip) {
		TotalTip = totalTip;
	}

	public int getItemSize() {
		return ItemSize;
	}

	public void setItemSize(int itemSize) {
		ItemSize = itemSize;
	}

	public int getItemProgress() {
		return ItemProgress;
	}

	public void setItemProgress(int itemProgress) {
		ItemProgress = itemProgress;
	}

	public String getItemTip() {
		return ItemTip;
	}

	public void setItemTip(String itemTip) {
		ItemTip = itemTip;
	}

	public List<IPTCheckResult> getIPTCheckResultList() {
		return IPTCheckResultList;
	}

	public void setIPTCheckResultList(List<IPTCheckResult> iPTCheckResultList) {
		IPTCheckResultList = iPTCheckResultList;
	}
}
