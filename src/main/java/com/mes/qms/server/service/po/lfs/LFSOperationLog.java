package com.mes.qms.server.service.po.lfs;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 系统重要操作日志
 * 
 * @author YouWang·Peng
 * @CreateTime 2021-11-16 13:39:42
 */
public class LFSOperationLog implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 来源ID
	 */
	public int SourceID = 0;
	/**
	 * 操作类型
	 */
	public int Type = 0;
	/**
	 * 操作类型
	 */
	public String TypeName = "";
	/**
	 * 操作者
	 */
	public int CreateID = 0;
	/**
	 * 操作者
	 */
	public String Creator = "";
	/**
	 * 操作时刻
	 */
	public Calendar CreateTime = Calendar.getInstance();
	/**
	 * 操作内容
	 */
	public String Content = "";

	public LFSOperationLog() {
		super();
	}

	public LFSOperationLog(int iD, int sourceID, int type, String typeName, int createID, String creator,
			Calendar createTime, String content) {
		super();
		ID = iD;
		SourceID = sourceID;
		Type = type;
		TypeName = typeName;
		CreateID = createID;
		Creator = creator;
		CreateTime = createTime;
		Content = content;
	}

	public String getTypeName() {
		return TypeName;
	}

	public int getSourceID() {
		return SourceID;
	}

	public void setSourceID(int sourceID) {
		SourceID = sourceID;
	}

	public void setTypeName(String typeName) {
		TypeName = typeName;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public int getCreateID() {
		return CreateID;
	}

	public void setCreateID(int createID) {
		CreateID = createID;
	}

	public String getCreator() {
		return Creator;
	}

	public void setCreator(String creator) {
		Creator = creator;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}
}
