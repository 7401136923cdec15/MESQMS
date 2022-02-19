package com.mes.qms.server.service.po.bfc;

import java.io.Serializable;

public class BFCMessageResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int status = 0;
	public String msg = "";

	public BFCMessageResult() {
	}

	public int getStatus() {
		return status;
	}

	public String getMsg() {
		return msg;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
