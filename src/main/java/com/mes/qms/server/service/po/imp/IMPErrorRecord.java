package com.mes.qms.server.service.po.imp;

import java.io.Serializable;

/**
 * 导入错误表
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-6-30 19:45:27
 * @LastEditTime 2020-6-30 19:45:31
 *
 */
public class IMPErrorRecord implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 导入表ID
	 */
	public int ParentID = 0;
	/**
	 * 导入主键ID
	 */
	public int PID = 0;
	/**
	 * 错误消息文本
	 */
	public String Message = "";

	public IMPErrorRecord() {
	}

	public IMPErrorRecord(int iD, int parentID, int pID, String message) {
		ID = iD;
		ParentID = parentID;
		PID = pID;
		Message = message;
	}

	public int getParentID() {
		return ParentID;
	}

	public void setParentID(int parentID) {
		ParentID = parentID;
	}

	public int getPID() {
		return PID;
	}

	public void setPID(int pID) {
		PID = pID;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}
}
