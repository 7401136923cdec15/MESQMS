package com.mes.qms.server.service.po.aps;

import java.io.Serializable;
import java.util.Calendar;

import com.mes.qms.server.service.mesenum.APSInstallCheckMode;

/**
 * 工位安装明细
 * 
 * @author ShrisJava
 *
 */
public class APSInstallation implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 序号
	 */
	public int ID = 0;

	/**
	 * 修程
	 */
	public int LineID = 0;

	/**
	 * 修程名称
	 */
	public String LineName = "";

	/**
	 * 工位（工段表）
	 */
	public int PartID = 0;

	/**
	 * 工位名称
	 */
	public String PartName = "";

	/**
	 * 车型
	 */
	public int ProductID = 0;

	/**
	 * 物料（部件）
	 */
	public double MaterialID = 0;
	/**
	 * 物料编码（部件）
	 */
	public String MaterialNo = "";

	/**
	 * 物料名称（部件）
	 */
	public String MaterialName = "";

	/**
	 * ==安装检查 0:默认 1仅检查并提示 2必装影响本工位转序 3必装影响上工位转序 4不检查
	 */
	public int InstallCheckMode = APSInstallCheckMode.OnlyCheck.getValue();

	/**
	 * 创建人
	 */
	public int CreatorID = 0;

	public String Creator = "";

	/**
	 * 创建时间
	 */
	public Calendar CreateTime = Calendar.getInstance();

	public Calendar EditTime = Calendar.getInstance();

	public String Editor = "";

	public int EditorID = 0;

	public Calendar AuditTime = Calendar.getInstance();

	public String Auditor = "";

	public int AuditorID = 0;

	/**
	 * 状态
	 */
	public int Status = 0;

	public int getID() {
		return ID;
	}

	public int getLineID() {
		return LineID;
	}

	public String getLineName() {
		return LineName;
	}

	public int getPartID() {
		return PartID;
	}

	public String getPartName() {
		return PartName;
	}

	public int getProductID() {
		return ProductID;
	}

	public double getMaterialID() {
		return MaterialID;
	}

	public String getMaterialNo() {
		return MaterialNo;
	}

	public int getCreatorID() {
		return CreatorID;
	}

	public String getCreator() {
		return Creator;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public Calendar getEditTime() {
		return EditTime;
	}

	public String getEditor() {
		return Editor;
	}

	public int getEditorID() {
		return EditorID;
	}

	public Calendar getAuditTime() {
		return AuditTime;
	}

	public String getAuditor() {
		return Auditor;
	}

	public int getAuditorID() {
		return AuditorID;
	}

	public int getStatus() {
		return Status;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public void setLineID(int lineID) {
		LineID = lineID;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public void setPartID(int partID) {
		PartID = partID;
	}

	public void setPartName(String partName) {
		PartName = partName;
	}

	public void setProductID(int productID) {
		ProductID = productID;
	}

	public void setMaterialID(double materialID) {
		MaterialID = materialID;
	}

	public void setMaterialNo(String materialNo) {
		MaterialNo = materialNo;
	}

	public void setCreatorID(int creatorID) {
		CreatorID = creatorID;
	}

	public void setCreator(String creator) {
		Creator = creator;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
	}

	public void setEditor(String editor) {
		Editor = editor;
	}

	public void setEditorID(int editorID) {
		EditorID = editorID;
	}

	public void setAuditTime(Calendar auditTime) {
		AuditTime = auditTime;
	}

	public void setAuditor(String auditor) {
		Auditor = auditor;
	}

	public void setAuditorID(int auditorID) {
		AuditorID = auditorID;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public APSInstallation() {

	}

}
