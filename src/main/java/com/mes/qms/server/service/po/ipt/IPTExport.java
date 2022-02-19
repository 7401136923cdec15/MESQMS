package com.mes.qms.server.service.po.ipt;

import com.mes.qms.server.service.po.aps.APSBOMItem;
import com.mes.qms.server.service.po.ipt.IPTExport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 新版过程控制记录导出结构
 * 
 * @author YouWang·Peng
 * @CreateTime 2021-1-13 10:20:11
 * @LastEditTime 2021-1-13 10:20:14
 *
 */
public class IPTExport implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 车型
	 */
	public String ProductNo = "";
	/**
	 * 修程
	 */
	public String LineName = "";
	/**
	 * 工位
	 */
	public String PartName = "";
	/**
	 * 工序
	 */
	public String StepName = "";
	/**
	 * 编制
	 */
	public String Maker = "";
	/**
	 * 编制日期
	 */
	public String MakerDate = "";
	/**
	 * 会签
	 */
	public String JointlySign = "";
	/**
	 * 会签日期
	 */
	public String JointlySignDate = "";
	/**
	 * 审核
	 */
	public String Audit = "";
	/**
	 * 审核日期
	 */
	public String AuditDate = "";
	/**
	 * 批准
	 */
	public String Approval = "";
	/**
	 * 批准日期
	 */
	public String ApprovalDate = "";
	/**
	 * 车号
	 */
	public String PartNo = "";
	/**
	 * 名称
	 */
	public String Name = "";
	/**
	 * 编号
	 */
	public String Code = "";  
	/**
	 * 版本
	 */
	public String Version = "";
	/**
	 * 人数
	 */
	public String PersonNumber = "";
	/**
	 * 作业时间
	 */
	public String WorkTime = "";
	/**
	 * 工艺文件
	 */
	public String CraftFile = "";
	/**
	 * 工序性质
	 */
	public String StepNature = "";
	/**
	 * 人员资质要求
	 */
	public String Requirement = "";
	/**
	 * 物料准备
	 */
	public String MaterialPrepare = "";
	/**
	 * 工具列表
	 */
	public List<IPTTool> IPTToolList = new ArrayList<IPTTool>();
	/**
	 * 项点列表
	 */
	public List<IPTItemExport> IPTItemExportList = new ArrayList<IPTItemExport>();
	/**
	 * 物料列表
	 */
	public List<APSBOMItem> APSBOMItemList = new ArrayList<APSBOMItem>();

	public String getProductNo() {
		return ProductNo;
	}

	public String getLineName() {
		return LineName;
	}

	public String getPartName() {
		return PartName;
	}

	public String getStepName() {
		return StepName;
	}

	public String getMaker() {
		return Maker;
	}

	public String getMakerDate() {
		return MakerDate;
	}

	public String getJointlySign() {
		return JointlySign;
	}

	public String getJointlySignDate() {
		return JointlySignDate;
	}

	public String getAudit() {
		return Audit;
	}

	public String getAuditDate() {
		return AuditDate;
	}

	public String getApproval() {
		return Approval;
	}

	public String getApprovalDate() {
		return ApprovalDate;
	}

	public String getPartNo() {
		return PartNo;
	}

	public String getName() {
		return Name;
	}

	public String getCode() {
		return Code;
	}

	public String getVersion() {
		return Version;
	}

	public String getPersonNumber() {
		return PersonNumber;
	}

	public String getWorkTime() {
		return WorkTime;
	}

	public String getCraftFile() {
		return CraftFile;
	}

	public String getStepNature() {
		return StepNature;
	}

	public String getRequirement() {
		return Requirement;
	}

	public String getMaterialPrepare() {
		return MaterialPrepare;
	}

	public List<IPTTool> getIPTToolList() {
		return IPTToolList;
	}

	public List<IPTItemExport> getIPTItemExportList() {
		return IPTItemExportList;
	}

	public List<APSBOMItem> getAPSBOMItemList() {
		return APSBOMItemList;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public void setPartName(String partName) {
		PartName = partName;
	}

	public void setStepName(String stepName) {
		StepName = stepName;
	}

	public void setMaker(String maker) {
		Maker = maker;
	}

	public void setMakerDate(String makerDate) {
		MakerDate = makerDate;
	}

	public void setJointlySign(String jointlySign) {
		JointlySign = jointlySign;
	}

	public void setJointlySignDate(String jointlySignDate) {
		JointlySignDate = jointlySignDate;
	}

	public void setAudit(String audit) {
		Audit = audit;
	}

	public void setAuditDate(String auditDate) {
		AuditDate = auditDate;
	}

	public void setApproval(String approval) {
		Approval = approval;
	}

	public void setApprovalDate(String approvalDate) {
		ApprovalDate = approvalDate;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public void setName(String name) {
		Name = name;
	}

	public void setCode(String code) {
		Code = code;
	}

	public void setVersion(String version) {
		Version = version;
	}

	public void setPersonNumber(String personNumber) {
		PersonNumber = personNumber;
	}

	public void setWorkTime(String workTime) {
		WorkTime = workTime;
	}

	public void setCraftFile(String craftFile) {
		CraftFile = craftFile;
	}

	public void setStepNature(String stepNature) {
		StepNature = stepNature;
	}

	public void setRequirement(String requirement) {
		Requirement = requirement;
	}

	public void setMaterialPrepare(String materialPrepare) {
		MaterialPrepare = materialPrepare;
	}

	public void setIPTToolList(List<IPTTool> iPTToolList) {
		IPTToolList = iPTToolList;
	}

	public void setIPTItemExportList(List<IPTItemExport> iPTItemExportList) {
		IPTItemExportList = iPTItemExportList;
	}

	public void setAPSBOMItemList(List<APSBOMItem> aPSBOMItemList) {
		APSBOMItemList = aPSBOMItemList;
	}
}
