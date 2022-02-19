package com.mes.qms.server.service.po.ipt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 预检导出PDF配置
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-3-9 20:51:02
 * @LastEditTime 2020-3-9 20:51:05
 *
 */
public class IPTPDFConfig implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 配置名称
	 */
	public String ConfigName = "";
	/**
	 * 修程
	 */
	public int LineID = 0;
	/**
	 * 车型
	 */
	public int ProductID = 0;
	/**
	 * 局段
	 */
	public int CustomID = 0;
	/**
	 * 创建人ID
	 */
	public int CreateID = 0;
	/**
	 * 创建时刻
	 */
	public Calendar CreateTime = Calendar.getInstance();
	/**
	 * 激活/关闭
	 */
	public int Active = 0;
	// 辅助属性
	/**
	 * PDF部分集合
	 */
	public List<IPTPDFPart> IPTPDFPartList = new ArrayList<IPTPDFPart>();
	/**
	 * 修程
	 */
	public String LineName = "";
	/**
	 * 车型
	 */
	public String ProductNo = "";
	/**
	 * 局段
	 */
	public String CustomName = "";
	/**
	 * 创建人
	 */
	public String CreateName = "";

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getConfigName() {
		return ConfigName;
	}

	public void setConfigName(String configName) {
		ConfigName = configName;
	}

	public int getLineID() {
		return LineID;
	}

	public void setLineID(int lineID) {
		LineID = lineID;
	}

	public int getProductID() {
		return ProductID;
	}

	public void setProductID(int productID) {
		ProductID = productID;
	}

	public int getCustomID() {
		return CustomID;
	}

	public void setCustomID(int customID) {
		CustomID = customID;
	}

	public int getCreateID() {
		return CreateID;
	}

	public void setCreateID(int createID) {
		CreateID = createID;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public List<IPTPDFPart> getIPTPDFPartList() {
		return IPTPDFPartList;
	}

	public void setIPTPDFPartList(List<IPTPDFPart> iPTPDFPartList) {
		IPTPDFPartList = iPTPDFPartList;
	}

	public String getLineName() {
		return LineName;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	public String getCustomName() {
		return CustomName;
	}

	public void setCustomName(String customName) {
		CustomName = customName;
	}

	public String getCreateName() {
		return CreateName;
	}

	public void setCreateName(String createName) {
		CreateName = createName;
	}
}
