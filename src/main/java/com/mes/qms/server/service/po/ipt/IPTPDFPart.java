package com.mes.qms.server.service.po.ipt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * PDF配置-组成部分
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-3-9 20:57:28
 * @LastEditTime 2020-3-9 20:57:31
 *
 */
public class IPTPDFPart implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * PDF配置ID
	 */
	public int PDFConfigID = 0;
	/**
	 * 标题
	 */
	public String PartTitle = "";
	/**
	 * 顺序
	 */
	public int OrderNo = 0;
	// 辅助属性
	/**
	 * 标准集合
	 */
	public List<IPTPDFStandard> IPTPDFStandardList = new ArrayList<IPTPDFStandard>();

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getPartTitle() {
		return PartTitle;
	}

	public void setPartTitle(String partTitle) {
		PartTitle = partTitle;
	}

	public int getOrderNo() {
		return OrderNo;
	}

	public void setOrderNo(int orderNo) {
		OrderNo = orderNo;
	}

	public List<IPTPDFStandard> getIPTPDFStandardList() {
		return IPTPDFStandardList;
	}

	public void setIPTPDFStandardList(List<IPTPDFStandard> iPTPDFStandardList) {
		IPTPDFStandardList = iPTPDFStandardList;
	}

	public int getPDFConfigID() {
		return PDFConfigID;
	}

	public void setPDFConfigID(int pDFConfigID) {
		PDFConfigID = pDFConfigID;
	}
}
