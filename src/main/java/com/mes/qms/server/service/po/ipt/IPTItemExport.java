package com.mes.qms.server.service.po.ipt;

import java.io.Serializable;

/**
 * 项点导出结构
 */
public class IPTItemExport implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 项目
	 */
	public String Project = "";
	/**
	 * 序号
	 */
	public String SerialNumber = "";
	/**
	 * 作业顺序
	 */
	public String WorkOrder = "";
	/**
	 * 作业内容
	 */
	public String WorkContent = "";
	/**
	 * 质量要求及安全注意事项
	 */
	public String Standard = "";
	/**
	 * 图解
	 */
	public String Legend = "";
	/**
	 * 作业时间
	 */
	public String WorkTime = "";
	/**
	 * 自检结果
	 */
	public String SelfResult = "";
	/**
	 * 自检工作者(日期)
	 */
	public String SelftDate = "";
	/**
	 * 互检结果
	 */
	public String MutualResult = "";
	/**
	 * 互检工作者(日期)
	 */
	public String MutualDate = "";
	/**
	 * 专检结果
	 */
	public String SpecialResult = "";
	/**
	 * 专检工作者(日期)
	 */
	public String SpecialDate = "";

	public IPTItemExport() {
		super();
	}

	public IPTItemExport(String project, String serialNumber, String workOrder, String workContent, String standard,
			String legend, String workTime, String selfResult, String selftDate, String mutualResult, String mutualDate,
			String specialResult, String specialDate) {
		super();
		Project = project;
		SerialNumber = serialNumber;
		WorkOrder = workOrder;
		WorkContent = workContent;
		Standard = standard;
		Legend = legend;
		WorkTime = workTime;
		SelfResult = selfResult;
		SelftDate = selftDate;
		MutualResult = mutualResult;
		MutualDate = mutualDate;
		SpecialResult = specialResult;
		SpecialDate = specialDate;
	}

	public String getProject() {
		return Project;
	}

	public String getSerialNumber() {
		return SerialNumber;
	}

	public String getWorkOrder() {
		return WorkOrder;
	}

	public String getWorkContent() {
		return WorkContent;
	}

	public String getStandard() {
		return Standard;
	}

	public String getLegend() {
		return Legend;
	}

	public String getWorkTime() {
		return WorkTime;
	}

	public String getSelfResult() {
		return SelfResult;
	}

	public String getSelftDate() {
		return SelftDate;
	}

	public String getMutualResult() {
		return MutualResult;
	}

	public String getMutualDate() {
		return MutualDate;
	}

	public String getSpecialResult() {
		return SpecialResult;
	}

	public String getSpecialDate() {
		return SpecialDate;
	}

	public void setProject(String project) {
		Project = project;
	}

	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}

	public void setWorkOrder(String workOrder) {
		WorkOrder = workOrder;
	}

	public void setWorkContent(String workContent) {
		WorkContent = workContent;
	}

	public void setStandard(String standard) {
		Standard = standard;
	}

	public void setLegend(String legend) {
		Legend = legend;
	}

	public void setWorkTime(String workTime) {
		WorkTime = workTime;
	}

	public void setSelfResult(String selfResult) {
		SelfResult = selfResult;
	}

	public void setSelftDate(String selftDate) {
		SelftDate = selftDate;
	}

	public void setMutualResult(String mutualResult) {
		MutualResult = mutualResult;
	}

	public void setMutualDate(String mutualDate) {
		MutualDate = mutualDate;
	}

	public void setSpecialResult(String specialResult) {
		SpecialResult = specialResult;
	}

	public void setSpecialDate(String specialDate) {
		SpecialDate = specialDate;
	}
}
