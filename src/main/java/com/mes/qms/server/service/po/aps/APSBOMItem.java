package com.mes.qms.server.service.po.aps;

import java.io.Serializable;
import java.util.Calendar;

import com.mes.qms.server.service.mesenum.BPMStatus;
import com.mes.qms.server.service.po.mss.MSSBOMItem;

/**
 * 台车BOM
 * 
 * @author ShrisJava
 *
 */
public class APSBOMItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int ID = 0;

	/**
	 * BOM类型 检修/新造
	 */
	public int BOMType = 0;

	/**
	 * 
	 */
	public int FactoryID = 1900;

	public String WBSNo = "";

	public int OrderID = 0;

	public String PartNo = "";

	public int LineID = 0;

	public String LineName = "";

	public int ProductID = 0;

	public String ProductNo = "";

	public int CustomerID = 0;

	public String CustomerCode = "";

	public int PartID = 0;

	public String PartName = "";

	public int PartPointID = 0;

	public String PartPointName = "";

	public int MaterialID = 0;

	public String MaterialNo = "";

	public String MaterialName = "";

	public Double Number = 0.0;

	public int UnitID = 0;

	public String UnitText = "PC";

	/**
	 * 必换1/偶换2/计划外 9
	 */
	public int ReplaceType = 0;

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
	 * 是否超修程 X/空
	 */
	public int OverLine = 0;

	/**
	 * 是否互唤件 X/空
	 */
	public int PartChange = 0;

	/**
	 * 领料部门
	 */
	public String ReceiveDepart = "0001";

	/**
	 * 仓库号 1100新造 1200检修
	 */
	public int StockID = 0;

	/**
	 * 质量损失大类 报废001 返工返修002 停产003 内部质量收入004
	 */
	public int QTType = 0;
	/**
	 * 质量损失小类 设计差错01 工艺差错02 制造差错03 供方原因04 其他原因05
	 */
	public int QTItemType = 0;

	/**
	 * 客户供料 X/空
	 */
	public int CustomerMaterial = 0;

	public int AuditorID = 0;

	public String Auditor = "";

	public Calendar AuditTime = Calendar.getInstance();

	public int EditorID = 0;

	public String Editor = "";

	public Calendar EditTime = Calendar.getInstance();

	public int Status = 0;

	/**
	 * 相关需求编号(主台车BOM)
	 */
	public String RelaDemandNo = "";
	/**
	 * 标文本码
	 */
	public String TextCode = "";
	/**
	 * 工作中心
	 */
	public String WorkCenter = "";
	/**
	 * 删除标识
	 */
	public String DeleteID = "";
	/**
	 * 相关需求的项目编号
	 */
	public String SubRelaDemandNo = "";
	/**
	 * 评估类型
	 */
	public String AssessmentType = "常规新件";
	/**
	 * 附件标志
	 */
	public String AccessoryLogo = "";
	/**
	 * 检修件分类
	 */
	public String RepairPartClass = "";
	/**
	 * 备注
	 */
	public String Remark = "";
	/**
	 * 定容号组
	 */
	public String DingrongGroup = "";
	/**
	 * 修复旧件标识
	 */
	public String RepairCoreIdentification = "";
	/**
	 * 领料数量
	 */
	public int PickingQuantity = 0;
	/**
	 * 偶换率
	 */
	public double EvenExchangeRate = 0.0;
	/**
	 * 委托单位
	 */
	public String Client = "";
	/**
	 * 顺序号
	 */
	public int OrderNum = 0;

	// 2021-3-17 14:30:06新增
	/**
	 * 来源类型
	 */
	public int SourceType = 0;
	/**
	 * 来源ID
	 */
	public int SourceID = 0;
	/**
	 * 是否为差异项
	 */
	public int DifferenceItem = 0;
	/**
	 * 是否超定额
	 */
	public int OverQuota = 0;

	public APSBOMItem() {
	}

	public APSBOMItem(MSSBOMItem wBOMItem, int wLineID, int wProductID, int wCustomerID, int wOrderID, String wWBSNo,
			String wPartNo) {

		this.BOMType = wBOMItem.BOMType;
		this.LineID = wLineID;
		this.ProductID = wProductID;
		this.CustomerID = wCustomerID;
		this.OrderID = wOrderID;
		this.WBSNo = wWBSNo;
		this.PartNo = wPartNo;
		this.MaterialID = wBOMItem.MaterialID;
		this.MaterialNo = wBOMItem.MaterialNo;
		this.Number = (double) wBOMItem.MaterialNumber;
		this.OriginalType = wBOMItem.OriginalType;
		this.OutsourceType = wBOMItem.OutsourceType;
		this.DisassyType = wBOMItem.DisassyType;
		this.OverLine = 0;
		this.PartID = wBOMItem.PlaceID;
		this.PartPointID = wBOMItem.PartPointID;
		this.ReplaceType = wBOMItem.ReplaceType;
		this.StockID = getStockIDByBOMType(wBOMItem.BOMType);
		this.MaterialNo = wBOMItem.MaterialNo;
		this.UnitID = wBOMItem.UnitID;
		this.Remark = wBOMItem.Remark;
		this.Status = BPMStatus.Save.getValue();
	}

	public int getStockIDByBOMType(int wBOMType) {
		switch (wBOMType) {
		case 1:
			return 1100;
		case 2:
			return 1200;
		default:
			break;
		}
		return 0;
	}

	public int getBOMType() {
		return BOMType;
	}

	public int getDifferenceItem() {
		return DifferenceItem;
	}

	public int getOverQuota() {
		return OverQuota;
	}

	public void setDifferenceItem(int differenceItem) {
		DifferenceItem = differenceItem;
	}

	public void setOverQuota(int overQuota) {
		OverQuota = overQuota;
	}

	public void setBOMType(int bOMType) {
		BOMType = bOMType;
	}

	public int getFactoryID() {
		return FactoryID;
	}

	public void setFactoryID(int factoryID) {
		FactoryID = factoryID;
	}

	public String getWBSNo() {
		return WBSNo;
	}

	public void setWBSNo(String wBSNo) {
		WBSNo = wBSNo;
	}

	public int getOrderID() {
		return OrderID;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public int getLineID() {
		return LineID;
	}

	public void setLineID(int lineID) {
		LineID = lineID;
	}

	public String getLineName() {
		return LineName;
	}

	public String getRelaDemandNo() {
		return RelaDemandNo;
	}

	public String getTextCode() {
		return TextCode;
	}

	public String getWorkCenter() {
		return WorkCenter;
	}

	public String getDeleteID() {
		return DeleteID;
	}

	public String getSubRelaDemandNo() {
		return SubRelaDemandNo;
	}

	public String getAssessmentType() {
		return AssessmentType;
	}

	public String getAccessoryLogo() {
		return AccessoryLogo;
	}

	public String getRepairPartClass() {
		return RepairPartClass;
	}

	public String getRemark() {
		return Remark;
	}

	public String getDingrongGroup() {
		return DingrongGroup;
	}

	public String getRepairCoreIdentification() {
		return RepairCoreIdentification;
	}

	public int getPickingQuantity() {
		return PickingQuantity;
	}

	public double getEvenExchangeRate() {
		return EvenExchangeRate;
	}

	public String getClient() {
		return Client;
	}

	public int getOrderNum() {
		return OrderNum;
	}

	public int getSourceType() {
		return SourceType;
	}

	public int getSourceID() {
		return SourceID;
	}

	public void setRelaDemandNo(String relaDemandNo) {
		RelaDemandNo = relaDemandNo;
	}

	public void setTextCode(String textCode) {
		TextCode = textCode;
	}

	public void setWorkCenter(String workCenter) {
		WorkCenter = workCenter;
	}

	public void setDeleteID(String deleteID) {
		DeleteID = deleteID;
	}

	public void setSubRelaDemandNo(String subRelaDemandNo) {
		SubRelaDemandNo = subRelaDemandNo;
	}

	public void setAssessmentType(String assessmentType) {
		AssessmentType = assessmentType;
	}

	public void setAccessoryLogo(String accessoryLogo) {
		AccessoryLogo = accessoryLogo;
	}

	public void setRepairPartClass(String repairPartClass) {
		RepairPartClass = repairPartClass;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public void setDingrongGroup(String dingrongGroup) {
		DingrongGroup = dingrongGroup;
	}

	public void setRepairCoreIdentification(String repairCoreIdentification) {
		RepairCoreIdentification = repairCoreIdentification;
	}

	public void setPickingQuantity(int pickingQuantity) {
		PickingQuantity = pickingQuantity;
	}

	public void setEvenExchangeRate(double evenExchangeRate) {
		EvenExchangeRate = evenExchangeRate;
	}

	public void setClient(String client) {
		Client = client;
	}

	public void setOrderNum(int orderNum) {
		OrderNum = orderNum;
	}

	public void setSourceType(int sourceType) {
		SourceType = sourceType;
	}

	public void setSourceID(int sourceID) {
		SourceID = sourceID;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public int getProductID() {
		return ProductID;
	}

	public void setProductID(int productID) {
		ProductID = productID;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	public int getPartID() {
		return PartID;
	}

	public void setPartID(int partID) {
		PartID = partID;
	}

	public String getPartNo() {
		return PartNo;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
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

	public Double getNumber() {
		return Number;
	}

	public void setNumber(Double number) {
		Number = number;
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

	public int getReplaceType() {
		return ReplaceType;
	}

	public void setReplaceType(int replaceType) {
		ReplaceType = replaceType;
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

	public int getOverLine() {
		return OverLine;
	}

	public void setOverLine(int overLine) {
		OverLine = overLine;
	}

	public int getPartChange() {
		return PartChange;
	}

	public void setPartChange(int partChange) {
		PartChange = partChange;
	}

	public String getReceiveDepart() {
		return ReceiveDepart;
	}

	public void setReceiveDepart(String receiveDepart) {
		ReceiveDepart = receiveDepart;
	}

	public int getStockID() {
		return StockID;
	}

	public void setStockID(int stockID) {
		StockID = stockID;
	}

	public int getQTType() {
		return QTType;
	}

	public void setQTType(int qTType) {
		QTType = qTType;
	}

	public int getQTItemType() {
		return QTItemType;
	}

	public void setQTItemType(int qTItemType) {
		QTItemType = qTItemType;
	}

	public int getCustomerMaterial() {
		return CustomerMaterial;
	}

	public void setCustomerMaterial(int customerMaterial) {
		CustomerMaterial = customerMaterial;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getCustomerID() {
		return CustomerID;
	}

	public void setCustomerID(int customerID) {
		CustomerID = customerID;
	}

	public int getEditorID() {
		return EditorID;
	}

	public void setEditorID(int editorID) {
		EditorID = editorID;
	}

	public String getEditor() {
		return Editor;
	}

	public void setEditor(String editor) {
		Editor = editor;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public Calendar getEditTime() {
		return EditTime;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
	}

	public int getAuditorID() {
		return AuditorID;
	}

	public void setAuditorID(int auditorID) {
		AuditorID = auditorID;
	}

	public String getAuditor() {
		return Auditor;
	}

	public void setAuditor(String auditor) {
		Auditor = auditor;
	}

	public Calendar getAuditTime() {
		return AuditTime;
	}

	public void setAuditTime(Calendar auditTime) {
		AuditTime = auditTime;
	}

	public String getPartName() {
		return PartName;
	}

	public void setPartName(String partName) {
		PartName = partName;
	}

	public String getCustomerCode() {
		return CustomerCode;
	}

	public void setCustomerCode(String customerCode) {
		CustomerCode = customerCode;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
