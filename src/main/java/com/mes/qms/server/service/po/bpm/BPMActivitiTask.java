package com.mes.qms.server.service.po.bpm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//我的任务
public class BPMActivitiTask extends BPMActivitiHisTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Calendar CreateTime = Calendar.getInstance();

	public Boolean isSuspended = false;

	public String Recipients = ""; // 任务通知人

	public int delegationState = 0; // 委托任务状态 0：非委托任务 1：委托任务待办 2：委托任务已办

	public List<BPMActivitiHisTask> HisTaskList = new ArrayList<BPMActivitiHisTask>();// 历史任务列表

	public int getDelegationState() {
		return delegationState;
	}

	public void setDelegationState(int wDelegationState) {
		delegationState = wDelegationState;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public boolean getIsSuspended() {
		return isSuspended;
	}

	public void setSuspend(boolean wSuspend) {
		isSuspended = wSuspend;
	}

	public List<BPMActivitiHisTask> getHisTaskList() {
		return HisTaskList;
	}

	public void setHisTaskList(List<BPMActivitiHisTask> hisTaskList) {
		HisTaskList = hisTaskList;
	}

	public String getRecipients() {
		return Recipients;
	}

	public void setRecipients(String wRecipients) {
		Recipients = wRecipients;
	}

}
