package com.mes.qms.server.service.po.dms;

import java.io.Serializable;
import java.util.Calendar;

/// <summary>
/// 设备台账信息
/// </summary>
public class DMSDeviceLedger implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/// <summary>
	/// 设备ID
	/// </summary>
	public long ID;

	/// <summary>
	/// 申请单ID
	/// </summary>
	public long ApplyID;
	/// <summary>
	/// 设备号
	/// </summary>
	public String DeviceNo;

	/// <summary>
	/// 名称
	/// </summary>
	public String Name;

	/// <summary>
	/// 设备型号ID
	/// </summary>
	public long ModelID;
	/// <summary>
	/// 固定资产ID
	/// </summary>
	public int AssetID;
	/// <summary>
	/// 设备净值
	/// </summary>
	public double NetValue;
	/// <summary>
	/// 设备使用寿命
	/// </summary>
	public double DeviceLife;
	/// <summary>
	/// 设备残值 设备报废挽回价值（如：废铁价值）
	/// </summary>
	public double ScrapValue;
	/// <summary>
	/// 设备加工限制
	/// </summary>
	public long LimitCount;

	/// <summary>
	/// 设备所属部门
	/// </summary>
	public int BusinessUnitID;
	/// <summary>
	/// 设备所属生产基地ID
	/// </summary>
	public int BaseID;
	/// <summary>
	/// 设备所属生产基地下的工厂ID
	/// </summary>
	public int FactoryID;
	/// <summary>
	/// 车间ID
	/// </summary>
	public int WorkShopID;
	/// <summary>
	/// 产线ID
	/// </summary>
	public int LineID;
	/// <summary>
	/// 位置ID 可选
	/// </summary>
	public int PositionID;

	/// <summary>
	/// 录入时刻
	/// </summary>
	public long OperatorID;
	/// <summary>
	/// 录入时刻
	/// </summary>
	public Calendar OperatorTime;
	/// <summary>
	/// 设备使用状态 0：就绪（默认值 加入台账时为就绪状态） 1：使用 2：闲置 3：维修 4：保养 5：报废 6：封存
	/// </summary>
	public int Status;

	public DMSDeviceLedger() {
		OperatorTime = Calendar.getInstance();
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public long getApplyID() {
		return ApplyID;
	}

	public void setApplyID(long applyID) {
		ApplyID = applyID;
	}

	public String getDeviceNo() {
		return DeviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		DeviceNo = deviceNo;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public long getModelID() {
		return ModelID;
	}

	public void setModelID(long modelID) {
		ModelID = modelID;
	}

	public int getAssetID() {
		return AssetID;
	}

	public void setAssetID(int assetID) {
		AssetID = assetID;
	}

	public double getNetValue() {
		return NetValue;
	}

	public void setNetValue(double netValue) {
		NetValue = netValue;
	}

	public double getDeviceLife() {
		return DeviceLife;
	}

	public void setDeviceLife(double deviceLife) {
		DeviceLife = deviceLife;
	}

	public double getScrapValue() {
		return ScrapValue;
	}

	public void setScrapValue(double scrapValue) {
		ScrapValue = scrapValue;
	}

	public long getLimitCount() {
		return LimitCount;
	}

	public void setLimitCount(long limitCount) {
		LimitCount = limitCount;
	}

	public int getBusinessUnitID() {
		return BusinessUnitID;
	}

	public void setBusinessUnitID(int businessUnitID) {
		BusinessUnitID = businessUnitID;
	}

	public int getBaseID() {
		return BaseID;
	}

	public void setBaseID(int baseID) {
		BaseID = baseID;
	}

	public int getFactoryID() {
		return FactoryID;
	}

	public void setFactoryID(int factoryID) {
		FactoryID = factoryID;
	}

	public int getWorkShopID() {
		return WorkShopID;
	}

	public void setWorkShopID(int workShopID) {
		WorkShopID = workShopID;
	}

	public int getLineID() {
		return LineID;
	}

	public void setLineID(int lineID) {
		LineID = lineID;
	}

	public int getPositionID() {
		return PositionID;
	}

	public void setPositionID(int positionID) {
		PositionID = positionID;
	}

	public long getOperatorID() {
		return OperatorID;
	}

	public void setOperatorID(long operatorID) {
		OperatorID = operatorID;
	}

	public Calendar getOperatorTime() {
		return OperatorTime;
	}

	public void setOperatorTime(Calendar operatorTime) {
		OperatorTime = operatorTime;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}
}
