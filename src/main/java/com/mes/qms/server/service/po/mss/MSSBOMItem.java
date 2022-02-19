package com.mes.qms.server.service.po.mss;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MSSBOMItem implements Serializable {

	private static final long serialVersionUID = 1L;

	public int ID = 0;
	public int BOMID = 0;
	public int MaterialID = 0;
	public String MaterialNo = "";
	public String MaterialName = "";
	/**
	 * 备注
	 */
	public String Remark = "";
	/**
	 * 数量
	 */
	public double MaterialNumber = 0;
	public String ProductQD = "";
	public int CustomerID = 0;
	public int TypeID = 0; // 主材|辅料|工装|标准件|量具
	public float LossRatio = 0.0f; // 损耗率
	public float MaterialUnit = 0.0f;
	public float MaterialUnitRatio = 0.0f;
	public String DeviceNo = "";
	public String Author = "";
	public String Auditor = "";
	public Calendar EditTime = Calendar.getInstance();
	public Calendar AuditTime = Calendar.getInstance();
	public int Active = 0;
	public String Type = "";
	public int PartPointID = 0; // 工序ID
	public String PartPointName = ""; // 工序名称
	public int ParentID = 0;
	public List<MSSBOMItem> ItemList = new ArrayList<>(); // Item集合
	public int GradeID = 0;
	public int UnitID = 0;
	public String UnitText = "";
	/**
	 * BOM类型
	 */
	public int BOMType;
	/**
	 * 工位ID
	 */
	public int PlaceID;

	/**
	 * 必换1/偶换2
	 */
	public int ReplaceType = 0;
	/**
	 * 偶换率
	 */
	public float ReplaceRatio = 0.0f;
	/**
	 * 委外必修件1/委外偶修件2/自修必修3/自修偶修4/ 其他0
	 */
	public int OutsourceType = 0;
	/**
	 * 原拆原装要求 X/空
	 */
	public int OriginalType = 0;
	/**
	 * 是否拆解下车 X/空
	 */
	public int DisassyType = 0;
	/**
	 * 顺序
	 */
	public int OrderNum = 0;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getOrderNum() {
		return OrderNum;
	}

	public void setOrderNum(int orderNum) {
		OrderNum = orderNum;
	}

	public int getBOMID() {
		return BOMID;
	}

	public void setBOMID(int bOMID) {
		BOMID = bOMID;
	}

	public int getMaterialID() {
		return MaterialID;
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

	public int getTypeID() {
		return TypeID;
	}

	public void setTypeID(int typeID) {
		TypeID = typeID;
	}

	public float getLossRatio() {
		return LossRatio;
	}

	public void setLossRatio(float lossRatio) {
		LossRatio = lossRatio;
	}

	public float getMaterialUnit() {
		return MaterialUnit;
	}

	public void setMaterialUnit(float materialUnit) {
		MaterialUnit = materialUnit;
	}

	public float getMaterialUnitRatio() {
		return MaterialUnitRatio;
	}

	public void setMaterialUnitRatio(float materialUnitRatio) {
		MaterialUnitRatio = materialUnitRatio;
	}

	public String getDeviceNo() {
		return DeviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		DeviceNo = deviceNo;
	}

	public String getAuthor() {
		return Author;
	}

	public void setAuthor(String author) {
		Author = author;
	}

	public String getAuditor() {
		return Auditor;
	}

	public void setAuditor(String auditor) {
		Auditor = auditor;
	}

	public Calendar getEditTime() {
		return EditTime;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
	}

	public Calendar getAuditTime() {
		return AuditTime;
	}

	public void setAuditTime(Calendar auditTime) {
		AuditTime = auditTime;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public int getPartPointID() {
		return PartPointID;
	}

	public void setPartPointID(int partPointID) {
		PartPointID = partPointID;
	}

	public String getPartPointName() {
		return PartPointName;
	}

	public void setPartPointName(String partPointName) {
		PartPointName = partPointName;
	}

	public int getParentID() {
		return ParentID;
	}

	public void setParentID(int parentID) {
		ParentID = parentID;
	}

	public List<MSSBOMItem> getItemList() {
		return ItemList;
	}

	public void setItemList(List<MSSBOMItem> itemList) {
		ItemList = itemList;
	}

	public int getGradeID() {
		return GradeID;
	}

	public void setGradeID(int gradeID) {
		GradeID = gradeID;
	}

	public int getUnitID() {
		return UnitID;
	}

	public void setUnitID(int unitID) {
		UnitID = unitID;
	}

	public String getUnitText() {
		return UnitText;
	}

	public void setUnitText(String unitText) {
		UnitText = unitText;
	}

	public MSSBOMItem() {
		this.EditTime = Calendar.getInstance();
		this.AuditTime = Calendar.getInstance();
		this.Auditor = "";
		this.Author = "";
		this.DeviceNo = "";
		this.PartPointName = "";
		this.MaterialNo = "";
		this.MaterialName = "";
		this.UnitText = "";
		this.ItemList = new ArrayList<>();
	}

	public int getBOMType() {
		return BOMType;
	}

	public void setBOMType(int bOMType) {
		BOMType = bOMType;
	}

	public int getPlaceID() {
		return PlaceID;
	}

	public void setPlaceID(int placeID) {
		PlaceID = placeID;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public double getMaterialNumber() {
		return MaterialNumber;
	}

	public void setMaterialNumber(double materialNumber) {
		MaterialNumber = materialNumber;
	}

	public String getProductQD() {
		return ProductQD;
	}

	public void setProductQD(String productQD) {
		ProductQD = productQD;
	}

	public int getCustomerID() {
		return CustomerID;
	}

	public int getReplaceType() {
		return ReplaceType;
	}

	public void setReplaceType(int replaceType) {
		ReplaceType = replaceType;
	}

	public float getReplaceRatio() {
		return ReplaceRatio;
	}

	public void setReplaceRatio(float replaceRatio) {
		ReplaceRatio = replaceRatio;
	}

	public int getOutsourceType() {
		return OutsourceType;
	}

	public void setOutsourceType(int outsourceType) {
		OutsourceType = outsourceType;
	}

	public int getOriginalType() {
		return OriginalType;
	}

	public void setOriginalType(int originalType) {
		OriginalType = originalType;
	}

	public int getDisassyType() {
		return DisassyType;
	}

	public void setDisassyType(int disassyType) {
		DisassyType = disassyType;
	}

	public void setCustomerID(int customerID) {
		CustomerID = customerID;
	}

}
