package com.mes.qms.server.service.po.sfc;

import java.io.Serializable;

import com.mes.qms.server.service.po.bpm.BPMTaskBase;

/**
 * 积压物资退库申请
 */
public class SFCReturnOverMaterial extends BPMTaskBase implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 物料ID
	 */
	public int MaterialID = 0;
	/**
	 * 物料编码
	 */
	public String MaterialNo = "";
	/**
	 * 物料名称
	 */
	public String MaterialName = "";
	/**
	 * 退库原因
	 */
	public String Reason = "";
	/**
	 * 退库需求
	 */
	public String ReturnDemand = "";
	/**
	 * 工位ID
	 */
	public int PartID = 0;
	/**
	 * 工位名称
	 */
	public String PartName = "";
	/**
	 * 工位编码
	 */
	public String PartCode = "";

	public int TagTypes = 0;

	public SFCReturnOverMaterial() {
	}

	public SFCReturnOverMaterial(int materialID, String materialNo, String materialName, String reason) {
		super();
		MaterialID = materialID;
		MaterialNo = materialNo;
		MaterialName = materialName;
		Reason = reason;
	}

	public int getMaterialID() {
		return MaterialID;
	}

	public int getTagTypes() {
		return TagTypes;
	}

	public void setTagTypes(int tagTypes) {
		TagTypes = tagTypes;
	}

	public void setMaterialID(int materialID) {
		MaterialID = materialID;
	}

	public String getMaterialNo() {
		return MaterialNo;
	}

	public void setMaterialNo(String materialNo) {
		MaterialNo = materialNo;
	}

	public String getMaterialName() {
		return MaterialName;
	}

	public void setMaterialName(String materialName) {
		MaterialName = materialName;
	}

	public String getReason() {
		return Reason;
	}

	public void setReason(String reason) {
		Reason = reason;
	}

	public String getReturnDemand() {
		return ReturnDemand;
	}

	public void setReturnDemand(String returnDemand) {
		ReturnDemand = returnDemand;
	}

	public int getPartID() {
		return PartID;
	}

	public void setPartID(int partID) {
		PartID = partID;
	}

	public String getPartName() {
		return PartName;
	}

	public void setPartName(String partName) {
		PartName = partName;
	}

	public String getPartCode() {
		return PartCode;
	}

	public void setPartCode(String partCode) {
		PartCode = partCode;
	}
}
