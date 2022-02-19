package com.mes.qms.server.service.po.oms;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 生产命令票
 * 
 * @author PengYouWang
 * @CreateTime 2020年1月2日11:18:43
 * @LastEditTime 2020年1月2日11:18:46
 *
 */
public class OMSCommand implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 唯一编号
	 */
	public int ID;
	public int CustomerID;
	public String Customer = "";
	public String ContactCode = "";
	public String No = "";
	public int Status;
	public int LinkManID;
	public String LinkMan = "";
	public int EditorID;
	public String Editor = "";
	public Calendar EditTime = Calendar.getInstance();
	public int Active;
	public int AuditorID;
	public String Auditor = "";
	public Calendar AuditTime = Calendar.getInstance();
	public int CreatorID;
	public String Creator = "";
	public Calendar CreateTime = Calendar.getInstance();
	public int FactoryID;
	public String Factory = "";
	public int BusinessUnitID;
	public String BusinessUnit = "";
	public int FQTYPlan;
	public int FQTYActual;

	public OMSCommand() {
		EditTime.set(2000, 1, 1);
		AuditTime.set(2000, 1, 1);
		CreateTime.set(2000, 1, 1);
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

	public String getCustomer() {
		return Customer;
	}

	public void setCustomer(String customer) {
		Customer = customer;
	}

	public String getContactCode() {
		return ContactCode;
	}

	public void setContactCode(String contactCode) {
		ContactCode = contactCode;
	}

	public String getNo() {
		return No;
	}

	public void setNo(String no) {
		No = no;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public int getLinkManID() {
		return LinkManID;
	}

	public void setLinkManID(int linkManID) {
		LinkManID = linkManID;
	}

	public String getLinkMan() {
		return LinkMan;
	}

	public void setLinkMan(String linkMan) {
		LinkMan = linkMan;
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

	public Calendar getEditTime() {
		return EditTime;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
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

	public int getCreatorID() {
		return CreatorID;
	}

	public void setCreatorID(int creatorID) {
		CreatorID = creatorID;
	}

	public String getCreator() {
		return Creator;
	}

	public void setCreator(String creator) {
		Creator = creator;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public int getFactoryID() {
		return FactoryID;
	}

	public void setFactoryID(int factoryID) {
		FactoryID = factoryID;
	}

	public String getFactory() {
		return Factory;
	}

	public void setFactory(String factory) {
		Factory = factory;
	}

	public int getBusinessUnitID() {
		return BusinessUnitID;
	}

	public void setBusinessUnitID(int businessUnitID) {
		BusinessUnitID = businessUnitID;
	}

	public String getBusinessUnit() {
		return BusinessUnit;
	}

	public void setBusinessUnit(String businessUnit) {
		BusinessUnit = businessUnit;
	}

	public int getFQTYPlan() {
		return FQTYPlan;
	}

	public void setFQTYPlan(int fQTYPlan) {
		FQTYPlan = fQTYPlan;
	}

	public int getFQTYActual() {
		return FQTYActual;
	}

	public void setFQTYActual(int fQTYActual) {
		FQTYActual = fQTYActual;
	}
}
