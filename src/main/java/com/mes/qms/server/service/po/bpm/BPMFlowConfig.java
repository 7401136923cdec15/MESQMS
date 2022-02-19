package com.mes.qms.server.service.po.bpm;

import java.util.Calendar;

/**
 * 流程配置表
 * @author ShrisJava
 *
 */

public class BPMFlowConfig {

	public int ID;
	
	/**
	 * BPMFlowType 流程枚举
	 */
	public int Type;

	public String Name;

	/**
	 * 表名称
	 */
	public String TableName;

	/**
	 * 数据库名称
	 */
	public String DBName;

	/**
	 * 数据库类型
	 */
	public int DBType;
	

	public Calendar CreatTime = Calendar.getInstance();

	/**
	 * 配置的 工程师用户ID
	 */
	public int Engineer = 0;

	/**
	 * 是否激活
	 */
	public int Active = 1;

	public Calendar EditTime = Calendar.getInstance();
	
	
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

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getTableName() {
		return TableName;
	}

	public void setTableName(String tableName) {
		TableName = tableName;
	}

	public String getDBName() {
		return DBName;
	}

	public void setDBName(String dBName) {
		DBName = dBName;
	}

	public int getDBType() {
		return DBType;
	}

	public void setDBType(int dBType) {
		DBType = dBType;
	}

	public Calendar getCreatTime() {
		return CreatTime;
	}

	public void setCreatTime(Calendar creatTime) {
		CreatTime = creatTime;
	}

	public int getEngineer() {
		return Engineer;
	}

	public void setEngineer(int engineer) {
		Engineer = engineer;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public Calendar getEditTime() {
		return EditTime;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
	}

	
}
