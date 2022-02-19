package com.mes.qms.server.service.po.record;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 导出校验记录结果
 * 
 * @author PengYouWang
 * @CreateTime 2020-10-26 17:41:00
 * @LastEditTime 2020-10-26 17:41:14
 *
 */
public class IPTCheckResult implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 唯一编号
	 */
	public int ID = 0;
	/**
	 * 检验记录ID(外键)
	 */
	public int RecordID = 0;
	/**
	 * 校验内容
	 */
	public String CheckContent = "";
	/**
	 * 校验结果
	 */
	public String CheckResult = "";
	/**
	 * 校验时刻
	 */
	public Calendar CheckTime = Calendar.getInstance();

	// 辅助属性
	/**
	 * 操作人
	 */
	public String Operator = "";
	/**
	 * 车号
	 */
	public String PartNo = "";

	public IPTCheckResult() {
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getCheckContent() {
		return CheckContent;
	}

	public void setCheckContent(String checkContent) {
		CheckContent = checkContent;
	}

	public String getCheckResult() {
		return CheckResult;
	}

	public void setCheckResult(String checkResult) {
		CheckResult = checkResult;
	}

	public Calendar getCheckTime() {
		return CheckTime;
	}

	public void setCheckTime(Calendar checkTime) {
		CheckTime = checkTime;
	}

	public String getOperator() {
		return Operator;
	}

	public void setOperator(String operator) {
		Operator = operator;
	}

	public String getPartNo() {
		return PartNo;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}
}
