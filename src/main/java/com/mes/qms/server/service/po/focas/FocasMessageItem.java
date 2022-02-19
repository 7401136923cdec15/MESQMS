package com.mes.qms.server.service.po.focas;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * focas消息内容
 */
public class FocasMessageItem implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	private String time = "";
	private String title = "";
	private String url = "";

	public FocasMessageItem() {
		super();
	}

	@JSONField(name = "time")
	public String getTime() {
		return time;
	}

	@JSONField(name = "title")
	public String getTitle() {
		return title;
	}

	@JSONField(name = "url")
	public String getUrl() {
		return url;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setLink(String link) {
		this.url = link;
	}

}
