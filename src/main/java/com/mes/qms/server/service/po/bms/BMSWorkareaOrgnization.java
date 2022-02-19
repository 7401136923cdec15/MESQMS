package com.mes.qms.server.service.po.bms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 工区组织架构
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-7-25 13:59:10
 * @LastEditTime 2020-7-25 13:59:14
 *
 */
public class BMSWorkareaOrgnization implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 工区名称
	 */
	public String WorkareaName = "";
	/**
	 * 班组列表
	 */
	public List<BMSClass> ClassList = new ArrayList<>();
	/**
	 * 工区人数
	 */
	public int FQTYWorkarea = 0;

	public String getWorkareaName() {
		return WorkareaName;
	}

	public void setWorkareaName(String workareaName) {
		WorkareaName = workareaName;
	}

	public List<BMSClass> getClassList() {
		return ClassList;
	}

	public void setClassList(List<BMSClass> classList) {
		ClassList = classList;
	}

	public int getFQTYWorkarea() {
		return FQTYWorkarea;
	}

	public void setFQTYWorkarea(int fQTYWorkarea) {
		FQTYWorkarea = fQTYWorkarea;
	}
}
