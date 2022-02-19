package com.mes.qms.server.service.po.imp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 导入结果表
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-6-30 19:45:27
 * @LastEditTime 2020-6-30 19:45:31
 *
 */
public class IMPResultRecord implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 导入人
	 */
	public int OperatorID = 0;
	/**
	 * 导入时间
	 */
	public Calendar OperateTime = Calendar.getInstance();
	/**
	 * 导入类型
	 */
	public int ImportType = 0;
	/**
	 * 流水号
	 */
	public String Code = "";
	/**
	 * 文件导入后对应的主键
	 */
	public List<Integer> PID = new ArrayList<Integer>();
	/**
	 * 导入结果
	 */
	public int Result = 0;
	/**
	 * 文件名
	 */
	public String FileName = "";
	/**
	 * 记录行数
	 */
	public int DataCount = 0;
	/**
	 * 导入进度
	 */
	public int Progress = 0;
	/**
	 * 错误个数
	 */
	public int ErrorCount = 0;

	// 辅助属性
	public String Operator = "";

	public IMPResultRecord() {
	}

	public IMPResultRecord(int iD, int operatorID, Calendar operateTime, int importType, String code, List<Integer> pID,
			int result, String fileName, int dataCount, int errorCount) {
		if (pID == null) {
			pID = new ArrayList<Integer>();
		}
		ID = iD;
		OperatorID = operatorID;
		OperateTime = operateTime;
		ImportType = importType;
		Code = code;
		PID = pID;
		Result = result;
		FileName = fileName;
		DataCount = dataCount;
		ErrorCount = errorCount;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public List<Integer> getPID() {
		return PID;
	}

	public void setPID(List<Integer> pID) {
		PID = pID;
	}

	public int getOperatorID() {
		return OperatorID;
	}

	public void setOperatorID(int operatorID) {
		OperatorID = operatorID;
	}

	public Calendar getOperateTime() {
		return OperateTime;
	}

	public void setOperateTime(Calendar operateTime) {
		OperateTime = operateTime;
	}

	public int getImportType() {
		return ImportType;
	}

	public void setImportType(int importType) {
		ImportType = importType;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public int getResult() {
		return Result;
	}

	public void setResult(int result) {
		Result = result;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}

	public int getDataCount() {
		return DataCount;
	}

	public void setDataCount(int dataCount) {
		DataCount = dataCount;
	}

	public String getOperator() {
		return Operator;
	}

	public void setOperator(String operator) {
		Operator = operator;
	}

	public int getErrorCount() {
		return ErrorCount;
	}

	public void setErrorCount(int errorCount) {
		ErrorCount = errorCount;
	}

	public int getProgress() {
		return Progress;
	}

	public void setProgress(int progress) {
		Progress = progress;
	}
}
