package com.mes.qms.server.service.po.mbs;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 系统操作日志
 * 
 * @author YouWang·Peng
 * @CreateTime 2021-4-28 11:15:50
 */
public class MBSApiLog implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	
	public int ID=0;
	public int CompanyID=0;
	public int LoginID=0;
	public String ProjectName="";
	public String URI="";
	public String Method="";
	public String Params="";
	public String Result="";
	public Calendar RequestTime=Calendar.getInstance();
	public Calendar ResponseTime=Calendar.getInstance();
	public String RequestBody="";
	public int IntervalTime=0;
	public int ResponseStatus=0;

	public MBSApiLog() {
	}

	public int getID() {
		return ID;
	}

	public int getCompanyID() {
		return CompanyID;
	}

	public int getLoginID() {
		return LoginID;
	}

	public String getProjectName() {
		return ProjectName;
	}

	public String getURI() {
		return URI;
	}

	public String getMethod() {
		return Method;
	}

	public String getParams() {
		return Params;
	}

	public String getResult() {
		return Result;
	}

	public Calendar getRequestTime() {
		return RequestTime;
	}

	public Calendar getResponseTime() {
		return ResponseTime;
	}

	public String getRequestBody() {
		return RequestBody;
	}

	public int getIntervalTime() {
		return IntervalTime;
	}

	public int getResponseStatus() {
		return ResponseStatus;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public void setCompanyID(int companyID) {
		CompanyID = companyID;
	}

	public void setLoginID(int loginID) {
		LoginID = loginID;
	}

	public void setProjectName(String projectName) {
		ProjectName = projectName;
	}

	public void setURI(String uRI) {
		URI = uRI;
	}

	public void setMethod(String method) {
		Method = method;
	}

	public void setParams(String params) {
		Params = params;
	}

	public void setResult(String result) {
		Result = result;
	}

	public void setRequestTime(Calendar requestTime) {
		RequestTime = requestTime;
	}

	public void setResponseTime(Calendar responseTime) {
		ResponseTime = responseTime;
	}

	public void setRequestBody(String requestBody) {
		RequestBody = requestBody;
	}

	public void setIntervalTime(int intervalTime) {
		IntervalTime = intervalTime;
	}

	public void setResponseStatus(int responseStatus) {
		ResponseStatus = responseStatus;
	}

}
