package com.mes.qms.server.service.po.crm;

import java.io.Serializable;
import java.util.Calendar;

public class CRMSaleService implements Serializable {

	private static final long serialVersionUID = 1L;

	public int ID = 0;

	public int CustomerID = 0;

	public String ServiceName = "";

	public String ContentText = "";

	public String TypeText;

	public int Type = 0;

	public Calendar StartTime = Calendar.getInstance();

	public Calendar EndTime = Calendar.getInstance();

	public int ExecutorID = 0;

	public String Executor = "";

	public String Description = "";

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

	public String getServiceName() {
		return ServiceName;
	}

	public void setServiceName(String serviceName) {
		ServiceName = serviceName;
	}

	public String getContentText() {
		return ContentText;
	}

	public void setContentText(String contentText) {
		ContentText = contentText;
	}

	public String getTypeText() {
		return TypeText;
	}

	public void setTypeText(String typeText) {
		TypeText = typeText;
	}

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public Calendar getStartTime() {
		return StartTime;
	}

	public void setStartTime(Calendar startTime) {
		StartTime = startTime;
	}

	public Calendar getEndTime() {
		return EndTime;
	}

	public void setEndTime(Calendar endTime) {
		EndTime = endTime;
	}

	public int getExecutorID() {
		return ExecutorID;
	}

	public void setExecutorID(int executorID) {
		ExecutorID = executorID;
	}

	public String getExecutor() {
		return Executor;
	}

	public void setExecutor(String executor) {
		Executor = executor;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public CRMSaleService() {
		this.StartTime = Calendar.getInstance();
		this.EndTime = Calendar.getInstance();
	}
}
