package com.mes.qms.server.service.po.ipt;

import java.io.Serializable;

/**
 * PDF配置-标准
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-3-9 21:00:52
 * @LastEditTime 2020-3-9 21:00:55
 *
 */
public class IPTPDFStandard implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * PDF配置部分ID
	 */
	public int PDFPartID = 0;
	/**
	 * 标准ID
	 */
	public int StandardID = 0;
	/**
	 * 表类型
	 */
	public int TableType = 0;
	/**
	 * 顺序
	 */
	public int OrderNo = 0;
	/**
	 * 标题名称
	 */
	public String TitleName = "";
	/**
	 * 是否显示标题
	 */
	public boolean IsShowTitle = false;

	// 辅助属性
	/**
	 * 标准名称
	 */
	public String StandardName = "";

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getStandardID() {
		return StandardID;
	}

	public void setStandardID(int standardID) {
		StandardID = standardID;
	}

	public int getTableType() {
		return TableType;
	}

	public void setTableType(int tableType) {
		TableType = tableType;
	}

	public int getOrderNo() {
		return OrderNo;
	}

	public void setOrderNo(int orderNo) {
		OrderNo = orderNo;
	}

	public String getTitleName() {
		return TitleName;
	}

	public void setTitleName(String titleName) {
		TitleName = titleName;
	}

	public boolean isIsShowTitle() {
		return IsShowTitle;
	}

	public void setIsShowTitle(boolean isShowTitle) {
		IsShowTitle = isShowTitle;
	}

	public int getPDFPartID() {
		return PDFPartID;
	}

	public void setPDFPartID(int pDFPartID) {
		PDFPartID = pDFPartID;
	}

	public String getStandardName() {
		return StandardName;
	}

	public void setStandardName(String standardName) {
		StandardName = standardName;
	}
}
