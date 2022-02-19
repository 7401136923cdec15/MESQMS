package com.mes.qms.server.service.po.fpc;

import java.io.Serializable;
import java.util.Calendar;

/**
 * tcm传输的通用文件
 */
public class FPCCommonFile implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	public int ID = 0;
	public String FilePath = "";
	public String Code = "";
	public String FileName = "";
	public String DocRev = "";
	public Calendar CreateTime = Calendar.getInstance();

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getFilePath() {
		return FilePath;
	}

	public void setFilePath(String filePath) {
		FilePath = filePath;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}

	public String getDocRev() {
		return DocRev;
	}

	public void setDocRev(String docRev) {
		DocRev = docRev;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}
}
