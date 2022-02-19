package com.mes.qms.server.service.po.fpc;

import java.io.Serializable;

/**
 * 流程图点信息
 */
public class FPCFlowPoint implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	public String id = "";
	public String uuid = "";
	public String anchor = "";

	public String getId() {
		return id;
	}

	public String getUuid() {
		return uuid;
	}

	public String getAnchor() {
		return anchor;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setAnchor(String anchor) {
		this.anchor = anchor;
	}
}
