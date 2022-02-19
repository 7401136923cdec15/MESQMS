package com.mes.qms.server.service.po.pic;

import java.io.Serializable;

/**
 * MES通用进度数据
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-11-20 11:17:42
 * @LastEditTime 2020-11-20 11:17:45
 *
 */
public class SFCProgress implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 唯一标识
	 */
	public String UUID = "";
	/**
	 * 最大值
	 */
	public int Max = 0;
	/**
	 * 当前值
	 */
	public int Value = 0;
	/**
	 * 百分比
	 */
	public String Percent = "0%";

	public String getUUID() {
		return UUID;
	}

	public void setUUID(String uUID) {
		UUID = uUID;
	}

	public int getMax() {
		return Max;
	}

	public void setMax(int max) {
		Max = max;
	}

	public int getValue() {
		return Value;
	}

	public void setValue(int value) {
		Value = value;
	}

	public String getPercent() {
		return Percent;
	}

	public void setPercent(String percent) {
		Percent = percent;
	}
}
