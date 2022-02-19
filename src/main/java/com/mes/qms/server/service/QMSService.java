package com.mes.qms.server.service;

import java.util.List;

import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.aps.APSTaskStep;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.fpc.FPCRoutePart;

public interface QMSService {

	/**
	 * 根据订单、工位获取上工位ID集合
	 * 
	 * @param wLoginUser 登录人
	 * @param wOrderID   订单ID
	 * @param wStationID 工位ID
	 * @return
	 */
	public ServiceResult<List<Integer>> FPC_QueryPreStationIDList(BMSEmployee wLoginUser, int wOrderID, int wStationID);

	/**
	 * 判断某个工序任务是否能进行派工
	 * 
	 * @param wLoginUser     登录者
	 * @param wAPSTaskPartID 工序任务ID
	 * @return 判断结果工位集合
	 */
	public ServiceResult<String> SFC_JudgeTaskStepIsCanDo(BMSEmployee wLoginUser, APSTaskStep wAPSTaskStep,
			List<FPCRoutePart> wThisRoutePartList, OutResult<Integer> wErrorCode);

	/**
	 * 判断是否发起不合格评审
	 * 
	 * @param wAdminUser
	 * @param wTaskID
	 * @param iPTItemID
	 * @return
	 */
	public ServiceResult<Boolean> WDW_IsSendNCR(BMSEmployee wAdminUser, int wTaskID, int iPTItemID);

	/**
	 * 判断是否发起返修
	 * 
	 * @param wAdminUser
	 * @param wTaskID
	 * @param wIPTItemID
	 * @return
	 */
	public ServiceResult<Boolean> WDW_IsSendRepair(BMSEmployee wAdminUser, int wTaskID, int wIPTItemID);
}
