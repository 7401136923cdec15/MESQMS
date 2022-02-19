package com.mes.qms.server.service.po.focas;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * focas需要的统计图数据
 */
public class FocasResult implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 机车竣工数
	 */
	private String name = "";
	/**
	 * C6修竣工数
	 */
	private String count = "";

	public FocasResult() {
		super();
	}

	@JSONField(name = "name")
	public String getName() {
		return name;
	}

	@JSONField(name = "count")
	public String getCount() {
		return count;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCount(String count) {
		this.count = count;
	}
}
