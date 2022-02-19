package com.mes.qms.server.service.po.ipt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 预检项目详情(工序)
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-2-12 13:13:23
 * @LastEditTime 2020-2-12 13:13:27
 *
 */
public class IPTPreCheckItem implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 报告ID
	 */
	public int ReportID = 0;
	/**
	 * 工序ID
	 */
	public int ItemID = 0;
	/**
	 * 项目名称(工序)
	 */
	public String ItemName = "";
	/**
	 * 预检人
	 */
	public String PreChecker = "";
	/**
	 * 项点列表
	 */
	public List<IPTItem> IPTItemList = new ArrayList<IPTItem>();
	/**
	 * 检验值列表
	 */
	public List<IPTValue> IPTValueList = new ArrayList<IPTValue>();
	/**
	 * 问题项集合
	 */
	public List<IPTPreCheckProblem> IPTProblemList = new ArrayList<IPTPreCheckProblem>();

	public IPTPreCheckItem() {
	}

	public String getItemName() {
		return ItemName;
	}

	public void setItemName(String itemName) {
		ItemName = itemName;
	}

	public List<IPTItem> getIPTItemList() {
		return IPTItemList;
	}
	
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getReportID() {
		return ReportID;
	}

	public void setReportID(int reportID) {
		ReportID = reportID;
	}

	public int getItemID() {
		return ItemID;
	}

	public void setItemID(int itemID) {
		ItemID = itemID;
	}

	public void setIPTItemList(List<IPTItem> iPTItemList) {
		IPTItemList = iPTItemList;
	}

	public List<IPTValue> getIPTValueList() {
		return IPTValueList;
	}

	public void setIPTValueList(List<IPTValue> iPTValueList) {
		IPTValueList = iPTValueList;
	}

	public String getPreChecker() {
		return PreChecker;
	}

	public void setPreChecker(String preChecker) {
		PreChecker = preChecker;
	}

	public List<IPTPreCheckProblem> getIPTProblemList() {
		return IPTProblemList;
	}

	public void setIPTProblemList(List<IPTPreCheckProblem> iPTProblemList) {
		IPTProblemList = iPTProblemList;
	}
}
