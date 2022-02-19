package com.mes.qms.server.service.po.ipt;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 预检问题项
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-2-12 14:27:10
 * @LastEditTime 2020-5-15 20:46:49
 *
 */
public class IPTProblemAssess implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键ID
	 */
	public int ID = 0;
	/**
	 * 问题项ID
	 */
	public int ProblemID = 0;
	/**
	 * 创建人ID
	 */
	public int CreateID = 0;
	/**
	 * 创建人名称
	 */
	public String Creator = "";
	/**
	 * 创建时刻
	 */
	public Calendar CreateTime = Calendar.getInstance();
	/**
	 * 评审人ID
	 */
	public int AuditID = 0;
	/**
	 * 评审人名称
	 */
	public String Auditor = "";
	/**
	 * 评审意见
	 */
	public IPTSOP IPTSOP = new IPTSOP();
	/**
	 * 评审时刻
	 */
	public Calendar AuditTime = Calendar.getInstance();
	/**
	 * 状态
	 */
	public int Status = 0;

	// 辅助属性
	public IPTPreCheckProblem IPTPreCheckProblem = new IPTPreCheckProblem();

	public IPTProblemAssess() {
		Calendar wBaseTime = Calendar.getInstance();
		wBaseTime.set(2000, 0, 1);
		AuditTime = wBaseTime;
		CreateTime = wBaseTime;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getProblemID() {
		return ProblemID;
	}

	public void setProblemID(int problemID) {
		ProblemID = problemID;
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

	public IPTPreCheckProblem getIPTPreCheckProblem() {
		return IPTPreCheckProblem;
	}

	public void setIPTPreCheckProblem(IPTPreCheckProblem iPTPreCheckProblem) {
		IPTPreCheckProblem = iPTPreCheckProblem;
	}

	public int getAuditID() {
		return AuditID;
	}

	public void setAuditID(int auditID) {
		AuditID = auditID;
	}

	public String getAuditor() {
		return Auditor;
	}

	public void setAuditor(String auditor) {
		Auditor = auditor;
	}

	public IPTSOP getIPTSOP() {
		return IPTSOP;
	}

	public void setIPTSOP(IPTSOP iPTSOP) {
		IPTSOP = iPTSOP;
	}

	public Calendar getAuditTime() {
		return AuditTime;
	}

	public void setAuditTime(Calendar auditTime) {
		AuditTime = auditTime;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}
}
