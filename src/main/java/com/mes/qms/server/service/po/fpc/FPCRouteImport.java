package com.mes.qms.server.service.po.fpc;

import java.io.Serializable;

/**
 * 工艺BOP导入结构
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-6-26 15:50:10
 * @LastEditTime 2020-6-27 10:51:40
 *
 */
public class FPCRouteImport implements Serializable {

	private static final long serialVersionUID = 1L;
	// FPCRoute
	/**
	 * 工艺BOP编号
	 */
	public String BOPNo = "";
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
	public String CustomerName = "";
	// FPCRoutePart
	/**
	 * 工艺集编号
	 */
	public String BOPPartNo = "";
	/**
	 * 工艺集名称
	 */
	public String BOPPartName = "";
	/**
	 * 工位编号
	 */
	public String PartCode = "";
	/**
	 * 前工艺集编号
	 */
	public String PreBOPPartNo = "";
	/**
	 * 后工艺集编号
	 */
	public String NextBOPPartNo = "";
	// FPCRoutePartPoint
	/**
	 * 工序ID
	 */
	public String PartPointCode = "";
	/**
	 * 工序名称
	 */
	public String PartPointName = "";
	/**
	 * 物料编号
	 */
	public String MaterialNo = "";
	/**
	 * 单位
	 */
	public String Unit = "";
	/**
	 * 工序序号
	 */
	public String PartPointNo = "";
	/**
	 * 前工序ID
	 */
	public String PrePartPointID = "";
	/**
	 * 后工序ID
	 */
	public String NextPartPointID = "";
	/**
	 * 标准作业时间
	 */
	public String StandardWorkTime = "";

	public FPCRouteImport() {
	}

	public String getBOPNo() {
		return BOPNo;
	}

	public void setBOPNo(String bOPNo) {
		BOPNo = bOPNo;
	}

	public String getLineName() {
		return LineName;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public String getCustomerName() {
		return CustomerName;
	}

	public void setCustomerName(String customerName) {
		CustomerName = customerName;
	}

	public String getBOPPartNo() {
		return BOPPartNo;
	}

	public void setBOPPartNo(String bOPPartNo) {
		BOPPartNo = bOPPartNo;
	}

	public String getBOPPartName() {
		return BOPPartName;
	}

	public void setBOPPartName(String bOPPartName) {
		BOPPartName = bOPPartName;
	}

	public String getPartCode() {
		return PartCode;
	}

	public void setPartCode(String partCode) {
		PartCode = partCode;
	}

	public String getPreBOPPartNo() {
		return PreBOPPartNo;
	}

	public void setPreBOPPartNo(String preBOPPartNo) {
		PreBOPPartNo = preBOPPartNo;
	}

	public String getNextBOPPartNo() {
		return NextBOPPartNo;
	}

	public void setNextBOPPartNo(String nextBOPPartNo) {
		NextBOPPartNo = nextBOPPartNo;
	}

	public String getPartPointCode() {
		return PartPointCode;
	}

	public void setPartPointCode(String partPointCode) {
		PartPointCode = partPointCode;
	}

	public String getPartPointName() {
		return PartPointName;
	}

	public void setPartPointName(String partPointName) {
		PartPointName = partPointName;
	}

	public String getMaterialNo() {
		return MaterialNo;
	}

	public void setMaterialNo(String materialNo) {
		MaterialNo = materialNo;
	}

	public String getUnit() {
		return Unit;
	}

	public void setUnit(String unit) {
		Unit = unit;
	}

	public String getPartPointNo() {
		return PartPointNo;
	}

	public void setPartPointNo(String partPointNo) {
		PartPointNo = partPointNo;
	}

	public String getPrePartPointID() {
		return PrePartPointID;
	}

	public void setPrePartPointID(String prePartPointID) {
		PrePartPointID = prePartPointID;
	}

	public String getNextPartPointID() {
		return NextPartPointID;
	}

	public void setNextPartPointID(String nextPartPointID) {
		NextPartPointID = nextPartPointID;
	}

	public String getStandardWorkTime() {
		return StandardWorkTime;
	}

	public void setStandardWorkTime(String standardWorkTime) {
		StandardWorkTime = standardWorkTime;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}
}
