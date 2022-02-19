package com.mes.qms.server.service.po.focas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * focas消息返回
 */
public class FocasMessageResult implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	private String code = "";
	private int count = 0;
	private List<FocasMessageItem> data = new ArrayList<FocasMessageItem>();

	public FocasMessageResult() {
		super();
	}

	@JSONField(name = "code")
	public String getCode() {
		return code;
	}

	@JSONField(name = "count")
	public int getCount() {
		return count;
	}

	@JSONField(name = "data")
	public List<FocasMessageItem> getData() {
		return data;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setData(List<FocasMessageItem> data) {
		this.data = data;
	}

}
