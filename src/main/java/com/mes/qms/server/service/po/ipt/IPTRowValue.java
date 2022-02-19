package com.mes.qms.server.service.po.ipt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 预检导出PDF行数据
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-3-10 19:16:30
 * @LastEditTime 2020-3-10 19:16:36
 *
 */
public class IPTRowValue implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 是否加粗
	 */
	public boolean IsBold = false;
	/**
	 * 是否是灰色背景
	 */
	public boolean IsGrayBackground = false;
	/**
	 * 行的值集合
	 */
	public List<String> ValueList = new ArrayList<String>();

	public boolean isIsBold() {
		return IsBold;
	}

	public void setIsBold(boolean isBold) {
		IsBold = isBold;
	}

	public boolean isIsGrayBackground() {
		return IsGrayBackground;
	}

	public void setIsGrayBackground(boolean isGrayBackground) {
		IsGrayBackground = isGrayBackground;
	}

	public List<String> getValueList() {
		return ValueList;
	}

	public void setValueList(List<String> valueList) {
		ValueList = valueList;
	}
}
