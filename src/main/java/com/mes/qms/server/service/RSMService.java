package com.mes.qms.server.service;

import java.util.Calendar;
import java.util.List;

import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.aps.APSTaskPart;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.fpc.FPCPart;
import com.mes.qms.server.service.po.rsm.RSMTurnOrderTask;
import com.mes.qms.server.service.po.sfc.SFCTaskIPT;
import com.mes.qms.server.service.po.tcm.TCMTechChangeNotice;

/**
 * 转序服务
 * 
 * @author ShrisJava
 *
 */
public interface RSMService {

	/**
	 * 测试用
	 */
	void HandleTechCahngeTest(BMSEmployee wLoginUser, int wChangeLogID, List<Integer> wOrderIDList);

	ServiceResult<List<RSMTurnOrderTask>> RSM_QueryTurnOrderTaskList(BMSEmployee wLoginUser, int wTaskPartID);

	ServiceResult<List<FPCPart>> RSM_QueryNextStationList(BMSEmployee wLoginUser, int wOrderID, int wStationID);

	ServiceResult<Integer> RSM_SubmitTurnOrderApply(BMSEmployee wLoginUser, APSTaskPart wAPSTaskPart,
			List<Integer> wStationIDList);

	ServiceResult<List<APSTaskPart>> RSM_QueryAllTaskPartList(BMSEmployee wLoginUser);

	ServiceResult<Integer> RSM_AutoTurnOrder(BMSEmployee wAdminUser);

	ServiceResult<Integer> RSM_AutoPassApply(BMSEmployee wAdminUser);

	/**
	 * 获取可转序的工位列表
	 * 
	 * @param wLoginUser 用户
	 * @param wOrderID   订单ID
	 * @param wStationID 工位ID
	 * @param wErrorCode 错误码
	 * @return
	 */
	ServiceResult<List<FPCPart>> SFC_QueryNextStationList(BMSEmployee wLoginUser, int wOrderID, int wStationID);

	/**
	 * 提交查询所有转序单
	 * 
	 * @param wLoginUser
	 * @return
	 */
	ServiceResult<List<RSMTurnOrderTask>> RSM_QueryTurnOrderTaskList(BMSEmployee wLoginUser, int wOrderID,
			int wApplyStationID, int wTargetStationID, List<Integer> wStateIDList, Calendar wStartTime,
			Calendar wEndTime);

	/**
	 * 渲染工艺BOP已完成的工位列表
	 */
	ServiceResult<List<Integer>> RSM_QueryBOPDoneList(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * 查询单条转序单
	 */
	ServiceResult<RSMTurnOrderTask> RSM_QueryInfo(BMSEmployee wLoginUser, int wID);

	/**
	 * 查询检验员转序确认单列表
	 */
	ServiceResult<List<RSMTurnOrderTask>> RSM_QueryCheckerConfirmList(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime);

	/**
	 * 转序确认
	 */
	ServiceResult<Integer> RMS_PostConfirm(BMSEmployee wLoginUser, RSMTurnOrderTask wData);

	/**
	 * 自动删除导出文件夹
	 */
	void RSM_DeleteExport(BMSEmployee adminUser);

	/**
	 * 获取转序工位任务列表
	 */
	ServiceResult<List<APSTaskPart>> RSM_QueryAllTaskPartListNew(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime, String wPartNo, int wPartID);

	ServiceResult<List<RSMTurnOrderTask>> RSM_QueryTurnOrderTaskList(BMSEmployee wLoginUser, int wOperatorID,
			Calendar wStartTime, Calendar wEndTime);

	ServiceResult<List<RSMTurnOrderTask>> RSM_QueryTurnOrderTaskList(BMSEmployee wLoginUser, List<Integer> wClassListID,
			Calendar wStartTime, Calendar wEndTime);

	/**
	 * 关闭消息
	 */
	void RSM_AutoCloseMessage(BMSEmployee adminUser);

	/**
	 * 自动完工预检工位任务
	 */
//	void RSM_AutoFinishTaskPart(BMSEmployee adminUser);

	ServiceResult<Integer> APS_TriggerFinalTask(BMSEmployee wLoginUser, String wOrderIDs);

	/**
	 * 自动创建转序单(工位已完工，但转序单未自动生成)
	 */
	void RSM_AutoCreateTurnOrderForm(BMSEmployee wLoginUser);

	/**
	 * 自动处理工艺变更待办
	 */
	void RSM_HandelTechChange(BMSEmployee adminUser);

	/**
	 * 自动完工工序任务
	 */
	void RSM_FinishTaskStep(BMSEmployee adminUser);

	void HandleTechCahnge(BMSEmployee wLoginUser, TCMTechChangeNotice wTCMTechChangeNotice);

	/**
	 * 禁用已经被禁用的派工任务的派工消息
	 */
	void RSM_DisableDispatchMessage(BMSEmployee adminUser);

	void RSM_AutoFinishTaskPart(BMSEmployee wAdminUser, SFCTaskIPT wSFCTaskIPT);

	void RSM_AutoCalculateTrainHistory(BMSEmployee adminUser);

	/**
	 * 自动完成工序任务，自检、互检、专检都已完成的情况
	 */
	void RSM_AutoFinishTaskStep(BMSEmployee adminUser);

	void RSM_AutoUpdateTaskPart(BMSEmployee adminUser);
}
