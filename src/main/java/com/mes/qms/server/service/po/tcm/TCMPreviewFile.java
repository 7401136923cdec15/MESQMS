package com.mes.qms.server.service.po.tcm;

import java.io.Serializable;

/**
 * TCM文件预览结构
 */
public class TCMPreviewFile implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 错误码
	 */
	public int code = 0;
	/**
	 * 文件唯一ID
	 */
	public String sha1 = "";
	/**
	 * 文件预览地址
	 */
	public String url = "";

	public int getCode() {
		return code;
	}

	public String getSha1() {
		return sha1;
	}

	public String getUrl() {
		return url;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setSha1(String sha1) {
		this.sha1 = sha1;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
