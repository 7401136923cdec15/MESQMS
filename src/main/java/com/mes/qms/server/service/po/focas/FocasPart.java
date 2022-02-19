package com.mes.qms.server.service.po.focas;

import java.io.Serializable;
import java.util.Calendar;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * focas需要的台车部件数据
 */
public class FocasPart implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 行号
	 */
	private int ID = 0;
	/**
	 * 部件编码
	 */
	private String Code = "";
	/**
	 * 部件名称
	 */
	private String Name = "";
	/**
	 * 车号
	 */
	private String PartNo = "";
	/**
	 * 修程
	 */
	private String LineName = "";
	/**
	 * 车型
	 */
	private String ProductNo = "";
	/**
	 * 局段
	 */
	private String CustomerName = "";
	/**
	 * 编辑者
	 */
	private String Editor = "";
	/**
	 * 编辑时刻
	 */
	private Calendar EditTime = Calendar.getInstance();
	/**
	 * 部件厂家
	 */
	private String SupplierName = "";
	/**
	 * 部件型号
	 */
	private String SupplierProductNo = "";
	/**
	 * 部件编号
	 */
	private String SupplierPartNo = "";
	/**
	 * 是否有合格证
	 */
	private String Certificate = "";
	/**
	 * 是否有电子履历
	 */
	private String Record = "";
	/**
	 * 是否有二维码
	 */
	private String QRCode = "";
	/**
	 * 备注
	 */
	private String Remark = "";
	/**
	 * 图片路径(网络地址，分号隔开)
	 */
	private String ImagePath = "";

	public FocasPart() {
		super();
	}

	@JSONField(name = "ID")
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	@JSONField(name = "Code")
	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	@JSONField(name = "Name")
	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	@JSONField(name = "LineName")
	public String getLineName() {
		return LineName;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	@JSONField(name = "ProductNo")
	public String getProductNo() {
		return ProductNo;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	@JSONField(name = "CustomerName")
	public String getCustomerName() {
		return CustomerName;
	}

	public void setCustomerName(String customerName) {
		CustomerName = customerName;
	}

	@JSONField(name = "Editor")
	public String getEditor() {
		return Editor;
	}

	public void setEditor(String editor) {
		Editor = editor;
	}

	@JSONField(name = "EditTime")
	public Calendar getEditTime() {
		return EditTime;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
	}

	@JSONField(name = "SupplierName")
	public String getSupplierName() {
		return SupplierName;
	}

	public void setSupplierName(String supplierName) {
		SupplierName = supplierName;
	}

	@JSONField(name = "SupplierProductNo")
	public String getSupplierProductNo() {
		return SupplierProductNo;
	}

	public void setSupplierProductNo(String supplierProductNo) {
		SupplierProductNo = supplierProductNo;
	}

	@JSONField(name = "SupplierPartNo")
	public String getSupplierPartNo() {
		return SupplierPartNo;
	}

	public void setSupplierPartNo(String supplierPartNo) {
		SupplierPartNo = supplierPartNo;
	}

	@JSONField(name = "Certificate")
	public String getCertificate() {
		return Certificate;
	}

	public void setCertificate(String certificate) {
		Certificate = certificate;
	}

	@JSONField(name = "Record")
	public String getRecord() {
		return Record;
	}

	public void setRecord(String record) {
		Record = record;
	}

	@JSONField(name = "QRCode")
	public String getQRCode() {
		return QRCode;
	}

	public void setQRCode(String qRCode) {
		QRCode = qRCode;
	}

	@JSONField(name = "Remark")
	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	@JSONField(name = "ImagePath")
	public String getImagePath() {
		return ImagePath;
	}

	public void setImagePath(String imagePath) {
		ImagePath = imagePath;
	}

	@JSONField(name = "PartNo")
	public String getPartNo() {
		return PartNo;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}
}
