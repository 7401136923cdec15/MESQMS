package com.mes.qms.server.service.po.bms;

import java.util.ArrayList;
import java.util.List;

/**
 * 班组结构
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-7-25 14:02:57
 * @LastEditTime 2020-7-25 14:03:02
 *
 */
public class BMSClass {
	/**
	 * 班组名称
	 */
	public String ClassName = "";
	/**
	 * 正式
	 */
	public int FQTYRegularWorkers = 0;
	/**
	 * 实习
	 */
	public int FQTYIntern = 0;
	/**
	 * 劳务返聘
	 */
	public int FQTYReemployment = 0;
	/**
	 * 班组人数
	 */
	public int FQTYClass = 0;
	/**
	 * 在岗
	 */
	public int FQTYOnTheJob = 0;
	/**
	 * 负责工位
	 */
	public String ResponsibleStations = "";
	/**
	 * 班组成员
	 */
	public List<BMSEmployee> TeamMembers = new ArrayList<>();

	public String getClassName() {
		return ClassName;
	}

	public void setClassName(String className) {
		ClassName = className;
	}

	public int getFQTYRegularWorkers() {
		return FQTYRegularWorkers;
	}

	public void setFQTYRegularWorkers(int fQTYRegularWorkers) {
		FQTYRegularWorkers = fQTYRegularWorkers;
	}

	public int getFQTYIntern() {
		return FQTYIntern;
	}

	public void setFQTYIntern(int fQTYIntern) {
		FQTYIntern = fQTYIntern;
	}

	public int getFQTYReemployment() {
		return FQTYReemployment;
	}

	public void setFQTYReemployment(int fQTYReemployment) {
		FQTYReemployment = fQTYReemployment;
	}

	public int getFQTYClass() {
		return FQTYClass;
	}

	public void setFQTYClass(int fQTYClass) {
		FQTYClass = fQTYClass;
	}

	public int getFQTYOnTheJob() {
		return FQTYOnTheJob;
	}

	public void setFQTYOnTheJob(int fQTYOnTheJob) {
		FQTYOnTheJob = fQTYOnTheJob;
	}

	public String getResponsibleStations() {
		return ResponsibleStations;
	}

	public void setResponsibleStations(String responsibleStations) {
		ResponsibleStations = responsibleStations;
	}

	public List<BMSEmployee> getTeamMembers() {
		return TeamMembers;
	}

	public void setTeamMembers(List<BMSEmployee> teamMembers) {
		TeamMembers = teamMembers;
	}
}
