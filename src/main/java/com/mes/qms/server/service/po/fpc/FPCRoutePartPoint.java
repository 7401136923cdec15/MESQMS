package com.mes.qms.server.service.po.fpc;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FPCRoutePartPoint implements Serializable {

	private static final long serialVersionUID = 1L;

	public int ID = 0;

	public int RouteID = 0; // 工艺版本ID

	public int PartID = 0; // 工序组ID

	public int PartPointID = 0;

	public String Code = "";

	public int OrderID = 0; // 循序

	public Calendar CreateTime = Calendar.getInstance();

	public int CreatorID = 0;

	public String PartName = "";

	public String PartPointName = "";

	public String Creator = "";

	public String VersionNo = ""; // 工艺版本

	public String RouteName = ""; // 工艺名称

	public int PrevStepID = 0;// 上工序路线ID

	/**
	 * 标准工时
	 */
	public Double StandardPeriod = 0.0;

	/**
	 * 调整工时 新增字段
	 */
	public Double ActualPeriod = 0.0;

	/**
	 * 新增字段 满足工艺需求
	 */
	public String DefaultOrder = "";

	public Map<String, String> NextStepIDMap = new HashMap<String, String>();// 上工段路线ID

	public int MaterialID = 0;

	public String MaterialNo = "";

	public int UnitID = 0;

	public String UnitText = "";

	public String RoutePartCode = "";

	// 辅助属性
	public int NewPartID = 0;
	public String NewPartName = "";

	/**
	 * 版本信息
	 */
	public String Version = "";

	public FPCRoutePartPoint() {
		this.PartName = "";
		this.PartPointName = "";
		this.VersionNo = "";
		this.Creator = "";
		this.CreateTime = Calendar.getInstance();
	}

	public FPCRoutePartPoint Clone() {
		FPCRoutePartPoint wItem = new FPCRoutePartPoint();
		wItem.ID = this.ID;
		wItem.RouteID = this.RouteID;
		wItem.PartID = this.PartID;
		wItem.PartPointID = this.PartPointID;
		wItem.OrderID = this.OrderID;

		wItem.CreatorID = this.CreatorID;
		wItem.CreateTime = this.CreateTime;
		wItem.Creator = this.Creator;

		return wItem;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getNewPartID() {
		return NewPartID;
	}

	public String getVersion() {
		return Version;
	}

	public void setVersion(String version) {
		Version = version;
	}

	public void setNewPartID(int newPartID) {
		NewPartID = newPartID;
	}

	public int getRouteID() {
		return RouteID;
	}

	public String getNewPartName() {
		return NewPartName;
	}

	public void setNewPartName(String newPartName) {
		NewPartName = newPartName;
	}

	public void setRouteID(int routeID) {
		RouteID = routeID;
	}

	public int getPartID() {
		return PartID;
	}

	public void setPartID(int partID) {
		PartID = partID;
	}

	public int getPartPointID() {
		return PartPointID;
	}

	public void setPartPointID(int partPointID) {
		PartPointID = partPointID;
	}

	public int getOrderID() {
		return OrderID;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public int getCreatorID() {
		return CreatorID;
	}

	public void setCreatorID(int creatorID) {
		CreatorID = creatorID;
	}

	public String getPartName() {
		return PartName;
	}

	public void setPartName(String partName) {
		PartName = partName;
	}

	public String getPartPointName() {
		return PartPointName;
	}

	public void setPartPointName(String partPointName) {
		PartPointName = partPointName;
	}

	public String getCreator() {
		return Creator;
	}

	public void setCreator(String creator) {
		Creator = creator;
	}

	public String getVersionNo() {
		return VersionNo;
	}

	public void setVersionNo(String versionNo) {
		VersionNo = versionNo;
	}

	public String getRouteName() {
		return RouteName;
	}

	public void setRouteName(String routeName) {
		RouteName = routeName;
	}

	public int getPrevStepID() {
		return PrevStepID;
	}

	public void setPrevStepID(int prevStepID) {
		PrevStepID = prevStepID;
	}

	public Map<String, String> getNextStepIDMap() {
		return NextStepIDMap;
	}

	public void setNextStepIDMap(Map<String, String> nextStepIDMap) {
		NextStepIDMap = nextStepIDMap;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public Double getStandardPeriod() {
		return StandardPeriod;
	}

	public void setStandardPeriod(Double standardPeriod) {
		StandardPeriod = standardPeriod;
	}

	public Double getActualPeriod() {
		return ActualPeriod;
	}

	public void setActualPeriod(Double actualPeriod) {
		ActualPeriod = actualPeriod;
	}

	public String getDefaultOrder() {
		return DefaultOrder;
	}

	public void setDefaultOrder(String defaultOrder) {
		DefaultOrder = defaultOrder;
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

	public String getRoutePartCode() {
		return RoutePartCode;
	}

	public void setRoutePartCode(String routePartCode) {
		RoutePartCode = routePartCode;
	}
}
