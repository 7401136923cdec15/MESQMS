package com.mes.qms.server.serviceimpl.dao;

import java.util.Calendar;
import java.util.List;

import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bpm.BPMTaskBase;

public interface TaskBaseDAO {

	/**
	 * 获取待办任务
	 * @param wLoginUser
	 * @param wResponsorID
	 * @param wErrorCode
	 * @return
	 */
	List<BPMTaskBase> BPM_GetUndoTaskList(BMSEmployee wLoginUser, int wResponsorID, OutResult<Integer> wErrorCode);

	/**
	 * 获取已办任务
	 * @param wLoginUser
	 * @param wResponsorID
	 * @param wStartTime
	 * @param wEndTime
	 * @param wErrorCode
	 * @return
	 */
	
	List<BPMTaskBase> BPM_GetDoneTaskList(BMSEmployee wLoginUser, int wResponsorID, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode);

	/**
	 * 获取发起任务
	 * @param wLoginUser
	 * @param wResponsorID
	 * @param wStartTime
	 * @param wEndTime
	 * @param wErrorCode
	 * @return
	 */
	List<BPMTaskBase> BPM_GetSendTaskList(BMSEmployee wLoginUser, int wResponsorID, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode);

	/**
	 * 提交任务
	 * @param wLoginUser
	 * @param wTask
	 * @param wErrorCode
	 * @return
	 */
	BPMTaskBase BPM_UpdateTask(BMSEmployee wLoginUser, BPMTaskBase wTask, OutResult<Integer> wErrorCode);

	/**
	 * 获取任务by主键ID
	 * @param wLoginUser
	 * @param wTaskID
	 * @param wCode
	 * @param wErrorCode
	 * @return
	 */
	BPMTaskBase BPM_GetTaskInfo(BMSEmployee wLoginUser, int wTaskID, String wCode, OutResult<Integer> wErrorCode);

}
