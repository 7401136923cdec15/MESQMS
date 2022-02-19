//package com.mes.qms.server.service.po.fpc;
//
//import java.io.Serializable;
//import java.util.Calendar;
//
//public class FPCProductRoute implements Serializable {
//	private static final long serialVersionUID = 1L;
//
//	public int ID = 0;
//
//	public Calendar CreateTime = Calendar.getInstance();
//
//	public int CreatorID;
//
//	public Calendar EditTime = Calendar.getInstance();
//
//	public int EditorID = 0;
//
//	public int Status = 0; // 审批状态
//
//	public Calendar AuditTime = Calendar.getInstance();
//
//	public int AuditorID = 0;
//
//	public int ProductID = 0; // 产品ID
//
//	public int RouteID = 0; // 工艺路径
//
//	public String Factory = "";
//
//	public int LineID = 0;
//
//	public String Line = "";
//
//	public String ProductNo = "";
//
//	public String RouteName = "";
//
//	public String VersionNo = "";
//
//	public String BusinessUnit = "";
//
//	public String ProductType = "";
//
//	public String Creator = "";
//
//	public String Auditor = "";
//
//	public String Editor = "";
//
//	public String StatusText = ""; // 审批状态
//
//	public int Active = 0; // 状态
//
//	public String ProductName = "";
//
//	public int CustomerID = 0;
//
//	public String CustomerName = "";
//
//	public int getID() {
//		return ID;
//	}
//
//	public void setID(int iD) {
//		ID = iD;
//	}
//
//	public int getCustomerID() {
//		return CustomerID;
//	}
//
//	public String getCustomerName() {
//		return CustomerName;
//	}
//
//	public void setCustomerName(String customerName) {
//		CustomerName = customerName;
//	}
//
//	public int getLineID() {
//		return LineID;
//	}
//
//	public void setLineID(int lineID) {
//		LineID = lineID;
//	}
//
//	public void setCustomerID(int customerID) {
//		CustomerID = customerID;
//	}
//
//	public Calendar getCreateTime() {
//		return CreateTime;
//	}
//
//	public void setCreateTime(Calendar createTime) {
//		CreateTime = createTime;
//	}
//
//	public int getCreatorID() {
//		return CreatorID;
//	}
//
//	public void setCreatorID(int creatorID) {
//		CreatorID = creatorID;
//	}
//
//	public Calendar getEditTime() {
//		return EditTime;
//	}
//
//	public void setEditTime(Calendar editTime) {
//		EditTime = editTime;
//	}
//
//	public int getEditorID() {
//		return EditorID;
//	}
//
//	public void setEditorID(int editorID) {
//		EditorID = editorID;
//	}
//
//	public int getStatus() {
//		return Status;
//	}
//
//	public void setStatus(int status) {
//		Status = status;
//	}
//
//	public Calendar getAuditTime() {
//		return AuditTime;
//	}
//
//	public void setAuditTime(Calendar auditTime) {
//		AuditTime = auditTime;
//	}
//
//	public int getAuditorID() {
//		return AuditorID;
//	}
//
//	public void setAuditorID(int auditorID) {
//		AuditorID = auditorID;
//	}
//
//	public int getProductID() {
//		return ProductID;
//	}
//
//	public void setProductID(int productID) {
//		ProductID = productID;
//	}
//
//	public int getRouteID() {
//		return RouteID;
//	}
//
//	public void setRouteID(int routeID) {
//		RouteID = routeID;
//	}
//
//	public String getFactory() {
//		return Factory;
//	}
//
//	public void setFactory(String factory) {
//		Factory = factory;
//	}
//
//	public String getLine() {
//		return Line;
//	}
//
//	public void setLine(String line) {
//		Line = line;
//	}
//
//	public String getProductNo() {
//		return ProductNo;
//	}
//
//	public void setProductNo(String productNo) {
//		ProductNo = productNo;
//	}
//
//	public String getRouteName() {
//		return RouteName;
//	}
//
//	public void setRouteName(String routeName) {
//		RouteName = routeName;
//	}
//
//	public String getVersionNo() {
//		return VersionNo;
//	}
//
//	public void setVersionNo(String versionNo) {
//		VersionNo = versionNo;
//	}
//
//	public String getBusinessUnit() {
//		return BusinessUnit;
//	}
//
//	public void setBusinessUnit(String businessUnit) {
//		BusinessUnit = businessUnit;
//	}
//
//	public String getProductType() {
//		return ProductType;
//	}
//
//	public void setProductType(String productType) {
//		ProductType = productType;
//	}
//
//	public String getCreator() {
//		return Creator;
//	}
//
//	public void setCreator(String creator) {
//		Creator = creator;
//	}
//
//	public String getAuditor() {
//		return Auditor;
//	}
//
//	public void setAuditor(String auditor) {
//		Auditor = auditor;
//	}
//
//	public String getEditor() {
//		return Editor;
//	}
//
//	public void setEditor(String editor) {
//		Editor = editor;
//	}
//
//	public String getStatusText() {
//		return StatusText;
//	}
//
//	public void setStatusText(String statusText) {
//		StatusText = statusText;
//	}
//
//	public int getActive() {
//		return Active;
//	}
//
//	public void setActive(int active) {
//		Active = active;
//	}
//
//	public String getProductName() {
//		return ProductName;
//	}
//
//	public void setProductName(String productName) {
//		ProductName = productName;
//	}
//
//	public FPCProductRoute() {
//		this.Line = "";
//		this.ProductNo = "";
//		this.VersionNo = "";
//		this.Factory = "";
//		this.BusinessUnit = "";
//		this.ProductType = "";
//		this.Editor = "";
//		this.Creator = "";
//		this.Auditor = "";
//		this.CreateTime = Calendar.getInstance();
//		this.AuditTime = Calendar.getInstance();
//		this.EditTime = Calendar.getInstance();
//		this.StatusText = "";
//	}
//
//	public FPCProductRoute Clone() {
//		FPCProductRoute wItem = new FPCProductRoute();
//		wItem.ID = this.ID;
//
//		wItem.CreatorID = this.CreatorID;
//		wItem.EditorID = this.EditorID;
//		wItem.AuditorID = this.AuditorID;
//		wItem.Status = this.Status;
//		wItem.Active = this.Active;
//		wItem.Active = this.Active;
//		wItem.CreateTime = this.CreateTime;
//		wItem.AuditTime = this.AuditTime;
//		wItem.EditTime = this.EditTime;
//
//		wItem.ProductID = this.ProductID;
//		wItem.RouteID = this.RouteID;
//
//		wItem.Line = this.Line;
//		wItem.ProductNo = this.ProductNo;
//		wItem.VersionNo = this.VersionNo;
//		wItem.Factory = this.Factory;
//		wItem.BusinessUnit = this.BusinessUnit;
//		wItem.ProductType = this.ProductType;
//		wItem.Creator = this.Creator;
//		wItem.Editor = this.Editor;
//		wItem.Auditor = this.Auditor;
//		wItem.Status = this.Status;
//		return wItem;
//	}
//}
