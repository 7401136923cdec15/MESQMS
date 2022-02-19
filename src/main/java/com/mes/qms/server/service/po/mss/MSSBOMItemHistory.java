package com.mes.qms.server.service.po.mss;

import java.io.Serializable;

/**
 * 标准bom物料子项-历史记录
 * 
 * @author YouWang·Peng
 * @CreateTime 2021-6-28 10:17:06
 */
public class MSSBOMItemHistory implements Serializable {

	private static final long serialVersionUID = 1L;

	public int ID = 0;
	public int ProductID = 0;
	public int LineID = 0;
	public int CustomerID = 0;
	public int PlaceID;
	public int PartPointID = 0; // 工序ID
	public int MaterialID = 0;
	public String MaterialNo = "";
	public String MaterialName = "";
	public double MaterialNumber = 0;
	public int UnitID = 0;
	public String UnitText = "";

	public MSSBOMItemHistory(int iD, int productID, int lineID, int customerID, int placeID, int partPointID,
			int materialID, String materialNo, String materialName, double materialNumber, int unitID,
			String unitText) {
		super();
		ID = iD;
		ProductID = productID;
		LineID = lineID;
		CustomerID = customerID;
		PlaceID = placeID;
		PartPointID = partPointID;
		MaterialID = materialID;
		MaterialNo = materialNo;
		MaterialName = materialName;
		MaterialNumber = materialNumber;
		UnitID = unitID;
		UnitText = unitText;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
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

	public int getPartPointID() {
		return PartPointID;
	}

	public void setPartPointID(int partPointID) {
		PartPointID = partPointID;
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

	public MSSBOMItemHistory() {
	}

	public int getPlaceID() {
		return PlaceID;
	}

	public void setPlaceID(int placeID) {
		PlaceID = placeID;
	}

	public double getMaterialNumber() {
		return MaterialNumber;
	}

	public void setMaterialNumber(int materialNumber) {
		MaterialNumber = materialNumber;
	}

	public int getCustomerID() {
		return CustomerID;
	}

	public void setCustomerID(int customerID) {
		CustomerID = customerID;
	}

	public int getProductID() {
		return ProductID;
	}

	public int getLineID() {
		return LineID;
	}

	public void setProductID(int productID) {
		ProductID = productID;
	}

	public void setLineID(int lineID) {
		LineID = lineID;
	}

	public void setMaterialNumber(double materialNumber) {
		MaterialNumber = materialNumber;
	}

}
