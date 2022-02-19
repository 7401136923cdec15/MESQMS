package com.mes.qms.server.service.po.mss;

import com.mes.qms.server.service.po.mss.MSSPartConfig;
import com.mes.qms.server.service.po.mss.MSSPartItem;
import java.util.Calendar;

public class MSSPartItem extends MSSPartConfig {
	private static final long serialVersionUID = 1L;
	public int Type = 1;

	public Double Number = Double.valueOf(0.0D);

	public String SupplierName = "";

	public String SupplierProductNo = "";

	public String SupplierPartNo = "";

	public int OrderID = 0;

	public String OrderNo = "";

	public int AuditorID = 0;

	public String Auditor = "";

	public Calendar AuditTime = Calendar.getInstance();

	public int Status = 0;

	public int Certificate = 0;

	public int Record = 0;

	public int QRCode = 0;

	public int IsUsed = 0;

	public int ProductID = 0;

	public String PartNo = "";

	public int MaterailID = 0;
	public String MaterialNo = "";
	public String MaterilaName = "";

	public Double getNumber() {
		return this.Number;
	}

	public void setNumber(Double number) {
		this.Number = number;
	}

	public String getSupplierName() {
		return this.SupplierName;
	}

	public int getProductID() {
		return this.ProductID;
	}

	public String getPartNo() {
		return this.PartNo;
	}

	public void setPartNo(String partNo) {
		this.PartNo = partNo;
	}

	public void setProductID(int productID) {
		this.ProductID = productID;
	}

	public void setSupplierName(String supplierName) {
		this.SupplierName = supplierName;
	}

	public String getSupplierProductNo() {
		return this.SupplierProductNo;
	}

	public void setSupplierProductNo(String supplierProductNo) {
		this.SupplierProductNo = supplierProductNo;
	}

	public String getSupplierPartNo() {
		return this.SupplierPartNo;
	}

	public int getMaterailID() {
		return MaterailID;
	}

	public String getMaterialNo() {
		return MaterialNo;
	}

	public String getMaterilaName() {
		return MaterilaName;
	}

	public void setMaterailID(int materailID) {
		MaterailID = materailID;
	}

	public void setMaterialNo(String materialNo) {
		MaterialNo = materialNo;
	}

	public void setMaterilaName(String materilaName) {
		MaterilaName = materilaName;
	}

	public void setSupplierPartNo(String supplierPartNo) {
		this.SupplierPartNo = supplierPartNo;
	}

	public int getOrderID() {
		return this.OrderID;
	}

	public void setOrderID(int orderID) {
		this.OrderID = orderID;
	}

	public String getOrderNo() {
		return this.OrderNo;
	}

	public void setOrderNo(String orderNo) {
		this.OrderNo = orderNo;
	}

	public int getStatus() {
		return this.Status;
	}

	public void setStatus(int status) {
		this.Status = status;
	}

	public int getType() {
		return this.Type;
	}

	public void setType(int type) {
		this.Type = type;
	}

	public int getAuditorID() {
		return this.AuditorID;
	}

	public void setAuditorID(int auditorID) {
		this.AuditorID = auditorID;
	}

	public String getAuditor() {
		return this.Auditor;
	}

	public void setAuditor(String auditor) {
		this.Auditor = auditor;
	}

	public Calendar getAuditTime() {
		return this.AuditTime;
	}

	public void setAuditTime(Calendar auditTime) {
		this.AuditTime = auditTime;
	}

	public int getCertificate() {
		return this.Certificate;
	}

	public void setCertificate(int certificate) {
		this.Certificate = certificate;
	}

	public int getRecord() {
		return this.Record;
	}

	public void setRecord(int record) {
		this.Record = record;
	}

	public int getQRCode() {
		return this.QRCode;
	}

	public void setQRCode(int qRCode) {
		this.QRCode = qRCode;
	}

	public int getIsUsed() {
		return this.IsUsed;
	}

	public void setIsUsed(int isUsed) {
		this.IsUsed = isUsed;
	}
}

/*
 * Location: C:\Users\Shris\Desktop\新建文件夹
 * (5)\MESQMS(1).war!\WEB-INF\classes\com\mes\qms\server\service\po\mss\
 * MSSPartItem.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.2
 */