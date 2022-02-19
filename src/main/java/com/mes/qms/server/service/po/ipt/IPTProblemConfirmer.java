package com.mes.qms.server.service.po.ipt;

import com.mes.qms.server.service.po.ipt.IPTProblemConfirmer;
import java.io.Serializable;
import java.util.Calendar;

/**
 * 预检问题项确认人
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-6-22 13:55:50
 * @LastEditTime 2020-6-22 13:55:56
 *
 */
public class IPTProblemConfirmer implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 预检问题项ID
	 */
	public int ProblemID = 0;
	/**
	 * 确认人ID
	 */
	public int ConfirmerID = 0;
	/**
	 * 确认人名称
	 */
	public String ConfirmerName = "";
	/**
	 * 确认时刻
	 */
	public Calendar ConfirmTime = Calendar.getInstance();
	/**
	 * 0：未确认 1：已确认
	 */
	public int Status = 0;
	/**
	 * 备注
	 */
	public String Remark = "";

	public IPTProblemConfirmer() {
		ConfirmTime.set(2000, 0, 1);
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

	public int getConfirmerID() {
		return ConfirmerID;
	}

	public void setConfirmerID(int confirmerID) {
		ConfirmerID = confirmerID;
	}

	public String getConfirmerName() {
		return ConfirmerName;
	}

	public void setConfirmerName(String confirmerName) {
		ConfirmerName = confirmerName;
	}

	public Calendar getConfirmTime() {
		return ConfirmTime;
	}

	public void setConfirmTime(Calendar confirmTime) {
		ConfirmTime = confirmTime;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}
}
