package com.mes.qms.server.service.po.tcm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TCMMaterialChangeItems implements Serializable {

	private static final long serialVersionUID = 1L;

	public int ID = 0;
	public int ChangeLogID = 0;
	public int ChangeType = 0;
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
	public List<TCMMaterialChangeItems> ItemList = new ArrayList<>(); // Item集合
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
	public int OrderNum = 0;

	public String BOMNo1 = "";
	public String BOMNo2 = "";

	public String RouteNo1 = "";
	public String RouteNo2 = "";

	public int NewPartID = 0;
	public String NewPartName = "";

	/**
	 * 处理方案
	 */
	public String Methods = "";
	/**
	 * 附件
	 */
	public String Annex = "";

	public int RouteID1 = 0;
	public int RouteID2 = 0;

	public double OldMaterialNumber = 0.0;

	public String PropertyChangeText = "";

	/**
	 * 老的必换/偶换
	 */
	public int OldReplaceType = 0;
	/**
	 * 老的必修/偶修
	 */
	public int OldOutSourceType = 0;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getPropertyChangeText() {
		return PropertyChangeText;
	}

	public void setPropertyChangeText(String propertyChangeText) {
		PropertyChangeText = propertyChangeText;
	}

	public int getBOMID() {
		return BOMID;
	}

	public double getOldMaterialNumber() {
		return OldMaterialNumber;
	}

	public void setOldMaterialNumber(double oldMaterialNumber) {
		OldMaterialNumber = oldMaterialNumber;
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

	public String getAnnex() {
		return Annex;
	}

	public int getRouteID1() {
		return RouteID1;
	}

	public int getRouteID2() {
		return RouteID2;
	}

	public void setAnnex(String annex) {
		Annex = annex;
	}

	public void setRouteID1(int routeID1) {
		RouteID1 = routeID1;
	}

	public void setRouteID2(int routeID2) {
		RouteID2 = routeID2;
	}

	public String getMethods() {
		return Methods;
	}

	public void setMethods(String methods) {
		Methods = methods;
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

	public int getOrderNum() {
		return OrderNum;
	}

	public void setOrderNum(int orderNum) {
		OrderNum = orderNum;
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

	public List<TCMMaterialChangeItems> getItemList() {
		return ItemList;
	}

	public void setItemList(List<TCMMaterialChangeItems> itemList) {
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

	public TCMMaterialChangeItems() {
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

	public int getChangeLogID() {
		return ChangeLogID;
	}

	public int getChangeType() {
		return ChangeType;
	}

	public void setChangeLogID(int changeLogID) {
		ChangeLogID = changeLogID;
	}

	public void setChangeType(int changeType) {
		ChangeType = changeType;
	}

	public String getBOMNo1() {
		return BOMNo1;
	}

	public String getBOMNo2() {
		return BOMNo2;
	}

	public String getRouteNo1() {
		return RouteNo1;
	}

	public String getRouteNo2() {
		return RouteNo2;
	}

	public int getNewPartID() {
		return NewPartID;
	}

	public String getNewPartName() {
		return NewPartName;
	}

	public void setBOMNo1(String bOMNo1) {
		BOMNo1 = bOMNo1;
	}

	public void setBOMNo2(String bOMNo2) {
		BOMNo2 = bOMNo2;
	}

	public void setRouteNo1(String routeNo1) {
		RouteNo1 = routeNo1;
	}

	public void setRouteNo2(String routeNo2) {
		RouteNo2 = routeNo2;
	}

	public void setNewPartID(int newPartID) {
		NewPartID = newPartID;
	}

	public void setNewPartName(String newPartName) {
		NewPartName = newPartName;
	}

	public int getOldReplaceType() {
		return OldReplaceType;
	}

	public int getOldOutSourceType() {
		return OldOutSourceType;
	}

	public void setOldReplaceType(int oldReplaceType) {
		OldReplaceType = oldReplaceType;
	}

	public void setOldOutSourceType(int oldOutSourceType) {
		OldOutSourceType = oldOutSourceType;
	}
}
